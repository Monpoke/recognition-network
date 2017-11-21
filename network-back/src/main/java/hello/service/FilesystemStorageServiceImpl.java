package hello.service;

import hello.service.exceptions.StorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
public class FilesystemStorageServiceImpl implements StorageService {

    private final Path rootLocation;


    public FilesystemStorageServiceImpl() {
        this.rootLocation = Paths.get("data/storage");
    }

    @Override
    public void store(MultipartFile file) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }
            Files.copy(file.getInputStream(), this.rootLocation.resolve(filename),
                    StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e) {
            try {
                throw new StorageException("Failed to store file " + filename, e);
            } catch (StorageException e1) {
                e1.printStackTrace();
            }
        } catch (StorageException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(path -> this.rootLocation.relativize(path));
        }
        catch (IOException e) {
            try {
                throw new StorageException("Failed to read stored files", e);
            } catch (StorageException e1) {
                e1.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                try {
                    throw new StorageException(
                            "Could not read file: " + filename);
                } catch (StorageException e) {
                    e.printStackTrace();
                }

            }
        }
        catch (MalformedURLException e) {
            try {
                throw new StorageException("Could not read file: " + filename, e);
            } catch (StorageException e1) {
                e1.printStackTrace();
            }
        }
        return null;

    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        }
        catch (IOException e) {
            try {
                throw new StorageException("Could not initialize storage", e);
            } catch (StorageException e1) {
                e1.printStackTrace();
            }
        }
    }
}