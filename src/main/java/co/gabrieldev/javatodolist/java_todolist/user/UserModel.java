package co.gabrieldev.javatodolist.java_todolist.user;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name="db_users")
public class UserModel {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(unique = true)
    private String username;
    private String name;
    private String password;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void encryptPassword() {
        var passwordHash = BCrypt.withDefaults().hashToString(12, getPassword().toCharArray());
        this.password = passwordHash.toString();
    }

    public boolean passwordMatch(String password) {
        var result = BCrypt.verifyer().verify(password.toCharArray(), getPassword());
        return result.verified;
    }
}
