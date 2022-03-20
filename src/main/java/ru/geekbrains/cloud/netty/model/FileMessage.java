package ru.geekbrains.cloud.netty.model;

import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Data// дает геттеры полям если поля финальные(если не финальные дает и геттеры и сеттеры) lombok
public class FileMessage implements CloudMessage {

    private final byte[] bytes;
    private final long size;
    private final String name;

    public FileMessage(Path path) throws IOException {
        bytes = Files.readAllBytes(path);
        size = bytes.length;
        name = path.getFileName().toString();
    }


    @Override
    public MessageType getMessageType() {
        return MessageType.FILE;
    }
}
