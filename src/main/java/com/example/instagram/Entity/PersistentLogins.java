package com.example.instagram.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class PersistentLogins {
    @Id
    private String series;
    private String username;
    private String token;
    private LocalDateTime lastUsed;
}
