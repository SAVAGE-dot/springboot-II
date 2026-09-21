package com.ciberbus;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.junit.jupiter.api.Test;

class CiberbusApplicationTests {

    @Test
    void clasePrincipalExiste() {
        assertNotNull(CiberbusApplication.class);
    }

    @Test
    void inicializarBaseDeDatos() {
        String url = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Lima";
        try (Connection conn = DriverManager.getConnection(url, "root", "");
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS bd_reserva_buses CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;");
            stmt.executeUpdate("USE bd_reserva_buses;");
            
            String sql00 = Files.readString(Path.of("sql/00_bd_reserva_buses.sql"));
            for (String query : sql00.split(";")) {
                if (!query.isBlank()) {
                    try {
                        stmt.execute(query);
                    } catch (Exception e) {
                        // Ignore or log if already exists
                    }
                }
            }

            String sql01 = Files.readString(Path.of("sql/01_ampliar_usuario.sql"));
            for (String query : sql01.split(";")) {
                if (!query.isBlank()) {
                    try {
                        stmt.execute(query);
                    } catch (Exception e) {
                        // Ignore if columns already added
                    }
                }
            }
            System.out.println("Base de datos bd_reserva_buses inicializada correctamente.");
        } catch (Exception e) {
            System.err.println("No se pudo inicializar la BD automáticamente (puede que MySQL no esté encendido): " + e.getMessage());
        }
    }
}
