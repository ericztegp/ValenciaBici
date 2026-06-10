package es.gva.edu.iesjuandegaray.bicis;

import java.sql.*;

import java.util.Scanner;

public class StarWars {
    public static void main(String[] args) throws Exception {
        
        String url = "jdbc:mysql://database-valenbici-eric.cutfvgyj3osh.us-east-1.rds.amazonaws.com:3306/starwars";
        String user = "admin";
        String password = "12345678";
        
        Scanner sc = new Scanner(System.in);
        System.out.println("Elige un número de película (1-6):");
        int pelicula = sc.nextInt();
        
        Connection conn = DriverManager.getConnection(url, user, password);
        
        String sql = """
            SELECT c.id, c.name, c.height, c.mass, c.hair_color, c.skin_color
            FROM characters c
            JOIN character_films cf ON c.id = cf.id_character
            WHERE cf.id_film = ?
            """;
        
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, pelicula);
        
        ResultSet rs = ps.executeQuery();
        
        while (rs.next()) {
            System.out.println(
                rs.getInt("id") + " " +
                rs.getString("name") + " " +
                rs.getInt("height") + " " +
                rs.getFloat("mass") + " " +
                rs.getString("hair_color") + " " +
                rs.getString("skin_color")
            );
        }
        
        rs.close();
        ps.close();
        conn.close();
    }
}