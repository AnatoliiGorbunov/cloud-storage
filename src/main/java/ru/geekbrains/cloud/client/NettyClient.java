package ru.geekbrains.cloud.client;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import ru.geekbrains.cloud.netty.model.CloudMessage;
import ru.geekbrains.cloud.netty.model.FileMessage;
import ru.geekbrains.cloud.netty.model.FileRequest;
import ru.geekbrains.cloud.netty.model.ListMessage;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class NettyClient implements Initializable {

    public TextField clientPath;
    public TextField serverPath;
    public ListView<String> clientView;
    public ListView<String> serverView;

    private Path clientDir;
    private final Path serverDir = Paths.get("Server");



    private ObjectEncoderOutputStream oos;
    private ObjectDecoderInputStream ois;

    public void download(ActionEvent actionEvent) throws IOException {
        oos.writeObject(new FileRequest(serverView.getSelectionModel().getSelectedItem()));
    }

    public void upload(ActionEvent actionEvent) throws IOException {
        oos.writeObject(new FileMessage(clientDir.resolve(clientView.getSelectionModel().getSelectedItem())));
    }

    public void DeleteClientFile(ActionEvent actionEvent) throws IOException {
        Path path = Paths.get(String.valueOf(clientDir.resolve(clientView.getSelectionModel().getSelectedItem())));
        Files.delete(path);
        updateClientView();
    }

    private void updateClientView() {
        Platform.runLater(() -> {
            clientPath.setText(clientDir.toFile().getAbsolutePath());
            clientView.getItems().clear();
            clientView.getItems().add("...");
            clientView.getItems()
                    .addAll(clientDir.toFile().list());

        });
    }

    private void read() {
        try {

            while (true) {
                CloudMessage msg = (CloudMessage) ois.readObject();// в качестве объекта ожидаем клоуд мессадж
                // в зависимости от типа
                switch (msg.getMessageType()) {
                    case FILE:
                        FileMessage fm = (FileMessage) msg;
                        Files.write(clientDir.resolve(fm.getName()), fm.getBytes());//записываем байты
                        updateClientView();
                        break;
                    case LIST:
                        ListMessage lm = (ListMessage) msg;
                        Platform.runLater(() -> {
                            serverPath.setText(serverDir.toFile().getAbsolutePath());
                            serverView.getItems().clear();
                            serverView.getItems().add("...");
                            serverView.getItems().addAll(lm.getFiles());
                        });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initNetwork() {
        try {
            Socket socket = new Socket("localhost", 8189);
            oos = new ObjectEncoderOutputStream(socket.getOutputStream());
            ois = new ObjectDecoderInputStream(socket.getInputStream());
            clientDir = Paths.get("clientDir");
            updateClientView();
            Thread readThread = new Thread(this::read);
            readThread.setDaemon(true);
            readThread.start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initNetwork();
    }

}




