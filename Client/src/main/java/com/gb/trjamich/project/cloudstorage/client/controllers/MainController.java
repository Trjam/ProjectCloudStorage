package com.gb.trjamich.project.cloudstorage.client.controllers;

import com.gb.trjamich.project.cloudstorage.client.classes.Network;
import com.google.gson.Gson;
import common.FileList;
import common.Request;
import common.Response;
import common.User;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static com.gb.trjamich.project.cloudstorage.client.classes.Network.*;

public class MainController implements Initializable {
    //private ObservableList<FileList> severList = FXCollections.observableArrayList();
    private static List<FileList> serverFileList;
    @FXML
    public Button logoutBtn;
    @FXML
    public Button regBtn;
    @FXML
    public TextField clientPath;
    @FXML
    public TextField serverPath;
    @FXML
    public Button mkDirClient;
    @FXML
    public Button del;
    @FXML
    public Button mkDirServer;
    @FXML
    public Button delSrv;
    @FXML
    public Button renClient;
    @FXML
    public Button renSrv;
    @FXML
    private Button loginBtn;
    private Stage stage;
    Stage loginStage;
    private AuthController authController;
    private Network network;


    @FXML
    public TableView<FileList> tableServerView;
    @FXML
    public TableColumn<FileList, String> columnServerName;
    @FXML
    public TableColumn<FileList, String> columnServerType;
    @FXML
    public TableColumn<FileList, String> columnServerSize;
    @FXML
    private TableView<FileList> tableView;
    @FXML
    private TableColumn<FileList, String> columnName;
    @FXML
    private TableColumn<FileList, String> columnType;
    @FXML
    private TableColumn<FileList, String> columnSize;

    public static Socket socket;
    private Path currentClientPath = Path.of("C:\\");
    private static Path currentServerPath;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            stage = new Stage();
            stage.setOnCloseRequest(event -> {
                System.out.println("bye");
                if (socket != null && !socket.isClosed()) {

                }
            });
        });
        //System.out.println(Arrays.toString(new File("c:/").listFiles()));
        setAuthenticated(false);
        currentClientPath = Path.of("C:\\");
        clientPath.setText(currentClientPath.toString());
        columnName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFileName()));
        columnType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFileType()));
        columnSize.setCellValueFactory(this::getSimpleStringProperty);
        columnServerName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFileName()));
        columnServerType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFileType()));
        columnServerSize.setCellValueFactory(this::getSimpleStringProperty);
        tableServerView.getItems();
        System.out.println(tableServerView.getItems().toString());

        tableView.getItems().addAll(getFileList(currentClientPath));

    }

    private SimpleStringProperty getSimpleStringProperty(TableColumn.CellDataFeatures<FileList, String> c) {
        SimpleStringProperty str = null;
        if (c.getValue().getFileType().equals("Directory") || c.getValue().getFileName().equals("...")) {
            str = new SimpleStringProperty("");
        } else if (c.getValue().getFileSize() <= 500) {
            str = new SimpleStringProperty(String.format("%.2f Кб", c.getValue().getFileSize() * 1.0));
        } else if (c.getValue().getFileSize() > 500 && c.getValue().getFileSize() <= 500 * 1024) {
            str = new SimpleStringProperty(String.format("%.2f Мб", (c.getValue().getFileSize() / 1024.00)));
        } else if (c.getValue().getFileSize() > 500 * 1024 && c.getValue().getFileSize() <= 500 * 1024 * 1024) {
            str = new SimpleStringProperty(String.format("%.2f Гб", (c.getValue().getFileSize() / (1024.00 * 1024.00))));
        }
        return str;
    }

    private void connect() {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {

                System.out.println("поток стартовал");
                try {
                    //цикл аутентификации
                    //int i = 0;
                    while (!authenticated) {

                        Response response = new Gson().fromJson(readString(), Response.class);

                        if (response != null && response.getReqType().equals("auth")) {

                            if ("login".equals(response.getOperation())) {
                                if ("ok".equals(response.getStatus())) {
                                    System.out.println("login ок");
                                    clientUuid = response.getToken();
                                    setAuthenticated(true);
                                    Platform.runLater(() -> {
                                        authController.closeLogin();
                                        loginBtn.setVisible(false);
                                        logoutBtn.setVisible(true);
                                    });
                                    new Thread(() -> {
                                        serverFileList();
                                        try {
                                            Thread.sleep(5000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    ).start();


                                } else if ("fault".equals(response.getStatus())) {
                                    Platform.runLater(() -> {
                                        sendInfoAlert(response.getInfo());
                                    });


                                }
                            } else if (response.getOperation().equals("register")) {
                                if ("ок".equals(response.getStatus())) {
                                    System.out.println("reg ок");
                                } else if ("fault".equals(response.getStatus())) {
                                    System.out.println("reg fault: " + response.getInfo());
                                }
                            } else if (response.getOperation().equals("logout") && response.getStatus().equals("ok")) {
                                setAuthenticated(false);
                                break;
                            }
                        } else {
                            ;
                            break;
                        }

                    }
                    while (authenticated) {
                        System.out.println("основной цикл");


                        String str = readString();
                        System.out.println(str);
                        Response response = new Gson().fromJson(str, Response.class);

                        if (response != null && response.getReqType().equals("nav")) {
                            if ("getFileList".equals(response.getOperation())) {
                                if ("ok".equals(response.getStatus())) {
                                    currentServerPath = Path.of(response.getCSP());
                                    updateServerTableView(response.getList());
                                }

                            }
                            if ("cd".equals(response.getOperation())) {
                                if ("ok".equals(response.getStatus())) {
                                    currentServerPath = Path.of(response.getCSP());
                                    updateServerTableView(response.getList());
                                }
                            }

                            if ("mkDir".equals(response.getOperation())) {
                                if ("ok".equals(response.getStatus())) {
                                    currentServerPath = Path.of(response.getCSP());
                                    updateServerTableView(response.getList());
                                } else {
                                    sendInfoAlert(response.getInfo());
                                }
                            }

                            if ("del".equals(response.getOperation())) {
                                if ("ok".equals(response.getStatus())) {
                                    currentServerPath = Path.of(response.getCSP());
                                    updateServerTableView(response.getList());
                                } else {
                                    sendInfoAlert(response.getInfo());
                                }
                            }

                            if ("ren".equals(response.getOperation())) {
                                if ("ok".equals(response.getStatus())) {
                                    currentServerPath = Path.of(response.getCSP());
                                    updateServerTableView(response.getList());
                                } else {
                                    sendInfoAlert(response.getInfo());
                                }
                            }


                        }


                        break;
                    }
                    // }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {

                        socket.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createLoginWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = fxmlLoader.load();
            loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.setScene(new Scene(root, 300, 150));
            loginStage.setResizable(false);

            loginStage.initModality(Modality.APPLICATION_MODAL);
            loginStage.initStyle(StageStyle.UTILITY);


            authController = fxmlLoader.getController();
            authController.setController(this);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void startLogin() {
        if (loginStage == null) {
            createLoginWindow();
        }
        Platform.runLater(() -> {
            loginStage.show();
        });
    }

    public static ObservableList<FileList> getFileList(Path path) {
        File[] files = new File(path.toString()).listFiles();

        ObservableList<FileList> list = FXCollections.observableArrayList();

        assert files != null;
        for (File file : files) {
            if (file.isDirectory()) {
                list.add(FileList.builder()
                        .fileName(file.getName())
                        .fileType("Directory")
                        .build());
            } else {
                list.add(FileList.builder()
                        .fileName(file.getName())
                        .fileType("File")
                        .fileSize(file.length() / 1024)
                        .build());
            }
        }
        FXCollections.sort(list, Comparator.comparing(FileList::getFileType));
        if (path.getParent() != null) {
            list.add(0, FileList.builder()
                    .fileName("...")
                    .fileType("Go to parent directory")
                    .build());
        }
        return list;
    }


    @FXML
    public void changeCurrentPath(MouseEvent e) {
        FileList t = tableView.getSelectionModel().getSelectedItem();
        if ((t != null && e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2)) {
            if (t.getFileName().equals("...") && currentClientPath.getParent() != null) {
                currentClientPath = currentClientPath.getParent();
            } else if (t.getFileType().equals("Directory")) {
                currentClientPath = Path.of(String.valueOf(currentClientPath), tableView.getSelectionModel().getSelectedItem().getFileName());
            } else {
                return;
            }
            clientPath.setText(currentClientPath.toString());
            tableView.getItems().removeAll(tableView.getItems());
            tableView.getItems().addAll(getFileList(currentClientPath));
        }
    }

    @FXML
    public void changeCurrentServerPath(MouseEvent e) throws IOException {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        System.out.println(currentServerPath);
        FileList t = tableServerView.getSelectionModel().getSelectedItem();
        if ((t != null && e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2)) {
            if (t.getFileName().equals("...")) {
                writeString(new Gson().toJson(Request.builder()
                        .reqType("nav")
                        .operation("cd")
                        .srcPath(currentServerPath.toString())
                        .targetPath(currentServerPath.getParent().toString())
                        .token(clientUuid)
                        .user(User.builder()
                                .login(clientLogin)
                                .build())
                        .build(), Request.class));
            } else if (t.getFileType().equals("Directory")) {
                writeString(new Gson().toJson(Request.builder()
                        .reqType("nav")
                        .operation("cd")
                        .srcPath(currentServerPath.toString())
                        .targetPath(currentServerPath.toString() + File.separator + t.getFileName())
                        .token(clientUuid)
                        .user(User.builder()
                                .login(clientLogin)
                                .build())
                        .build(), Request.class));
            } else {
                return;
            }
        }
    }

    public void updateServerTableView(List<FileList> list) {
        ObservableList<FileList> observableList = FXCollections.observableArrayList();
        observableList.addAll(list);
        tableServerView.getItems().removeAll(tableServerView.getItems());
        tableServerView.getItems().addAll(observableList);
        Platform.runLater(() -> {
            serverPath.setText(currentServerPath.toString());
        });

    }

    public void login(String login, String password) {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        clientLogin = login;
        Request request = Request.builder()
                .reqType("auth")
                .operation("login")
                .user(User.builder()
                        .login(login)
                        .password(password)
                        .build())
                .build();
        try {
            writeString(new Gson().toJson(request, Request.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void serverFileList() {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        Request request = Request.builder()
                .user(User.builder()
                        .login(clientLogin)
                        .build())
                .token(clientUuid)
                .reqType("nav")
                .operation("getFileList")
                .build();
        if (currentServerPath != null) {
            request.setTargetPath(currentServerPath.toString());
        }
        try {
            writeString(new Gson().toJson(request, Request.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createDir(String name) {

        if (!Files.exists(Path.of(currentClientPath.toString(), name))) {
            try {
                Files.createDirectories(Path.of(currentClientPath.toString(), name));
            } catch (IOException e) {
                e.printStackTrace();
            }
            tableView.getItems().removeAll(tableView.getItems());
            tableView.getItems().addAll(getFileList(currentClientPath));
        } else {
            sendInfoAlert("Папка с таким именем уже существует.");
        }
    }

    private void sendInfoAlert(String s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Внимание");
        alert.setHeaderText("Внимание!");
        alert.setContentText(s);
        alert.show();
    }

    public void startCreateDir(ActionEvent e) {
        System.out.println(e.getSource());
        Dialog<String> dialog = new TextInputDialog("Введите название папки");
        dialog.setTitle("Создание каталога");
        dialog.setHeaderText("Введите имя папки, которую хотите создать.");

        Optional<String> result = dialog.showAndWait();
        String entered = "";

        if (result.isPresent()) {
            entered = result.get();
        }
        if (!entered.isEmpty()) {
            if (e.getSource().equals(mkDirClient)) {
                createDir(entered);
            } else {
                createDirServer(entered);
            }
        }
    }

    private void createDirServer(String s) {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        Request request = Request.builder()
                .reqType("nav")
                .operation("mkDir")
                .path(currentServerPath.toString())
                .targetPath(currentServerPath.toString() + File.separator + s)
                .token(clientUuid)
                .user(User.builder()
                        .login(clientLogin)
                        .build())
                .build();
        try {
            writeString(new Gson().toJson(request, Request.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startDel(ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete file or directory");
        String s = "Confirm delete of file or directory.";
        alert.setContentText(s);

        Optional<ButtonType> result = alert.showAndWait();

        if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
            if (e.getSource().equals(del)) {
                deleteClient(tableView.getSelectionModel().getSelectedItem().getFileName());
            } else {
                deleteServer(tableServerView.getSelectionModel().getSelectedItem().getFileName());
            }

        }

    }

    private void deleteServer(String s) {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        Request request = Request.builder()
                .reqType("nav")
                .operation("del")
                .path(currentServerPath.toString())
                .targetPath(currentServerPath.toString() + File.separator + s)
                .token(clientUuid)
                .user(User.builder()
                        .login(clientLogin)
                        .build())
                .build();
        try {
            writeString(new Gson().toJson(request, Request.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteClient(String s) {
        if (Files.exists(Path.of(currentClientPath.toString(), s))) {
            try {
                Files.delete(Path.of(currentClientPath.toString(), s));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        tableView.getItems().removeAll(tableView.getItems());
        tableView.getItems().addAll(getFileList(currentClientPath));
    }

    public void startRename(ActionEvent e) {
        Dialog<String> dialog = new TextInputDialog("Введите новое имя");
        dialog.setTitle("Переименовать");
        dialog.setHeaderText("Введите новон имя для файла или папки.");

        Optional<String> result = dialog.showAndWait();
        String entered = "";

        if (result.isPresent()) {
            entered = result.get();
        }

        if (!entered.isEmpty()) {
            if (e.getSource().equals(renClient)) {
                renameClient(tableView.getSelectionModel().getSelectedItem().getFileName(), entered);
            } else {
                renameServer(tableServerView.getSelectionModel().getSelectedItem().getFileName(), entered);
            }
        }
    }

    private void renameServer(String fileName, String newFileName) {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        Request request = Request.builder()
                .reqType("nav")
                .operation("ren")
                .path(currentServerPath.toString())
                .srcPath(currentServerPath.toString() + File.separator + fileName)
                .targetPath(currentServerPath.toString() + File.separator + newFileName)
                .token(clientUuid)
                .user(User.builder()
                        .login(clientLogin)
                        .build())
                .build();
        try {
            writeString(new Gson().toJson(request, Request.class));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void renameClient(String fileName, String newFileName) {
        if (Files.exists(Path.of(currentClientPath.toString(), fileName))) {
            try {
                Files.move(Path.of(currentClientPath.toString(), fileName),
                        Path.of(currentClientPath.toString(), newFileName), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        tableView.getItems().removeAll(tableView.getItems());
        tableView.getItems().addAll(getFileList(currentClientPath));
    }

    public void uploadFile(ActionEvent e) {
        if (socket == null || socket.isClosed()) {
            connect();
        }

            String filename = tableView.getSelectionModel().getSelectedItem().getFileName();
            File file = new File(currentClientPath + File.separator + filename);

            if (!file.exists()) {
                try {
                    throw new FileNotFoundException();
                } catch (FileNotFoundException exception) {
                    exception.printStackTrace();
                }
            }

            Request request = Request.builder()
                    .reqType("file")
                    .operation("upload")
                    .fileLength(file.length())
                    .path(currentServerPath.toString())
                    .targetPath(currentServerPath.toString() + File.separator + tableView.getSelectionModel().getSelectedItem().getFileName())
                    .token(clientUuid)
                    .user(User.builder()
                            .login(clientLogin)
                            .build())
                    .build();




    }

    public void downloadFile(ActionEvent e) {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        if (Files.exists(Path.of(currentClientPath.toString() + File.separator + tableView.getSelectionModel().getSelectedItem().getFileName()))) {
            Request request = Request.builder()
                    .reqType("file")
                    .operation("download")
                    .path(currentServerPath.toString())
                    .targetPath(currentServerPath.toString() + File.separator + tableView.getSelectionModel().getSelectedItem().getFileName())
                    .token(clientUuid)
                    .user(User.builder()
                            .login(clientLogin)
                            .build())
                    .build();
            try {
                writeString(new Gson().toJson(request, Request.class));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }


    }
}