package TEMA;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;
import java.util.Scanner;

public class MainApp {
    public static void afisare_tabela(ResultSet rs, String mesaj) {
        System.out.println("\n---"+mesaj+"---");
        try {
            rs.beforeFirst();while (rs.next())
                System.out.println("id=" + rs.getInt(1) + ", nume=" + rs.getString(2) + ",varsta="
                        + rs.getInt(3));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void adaugare(ResultSet rs, Scanner scanner) {

        System.out.print("Introduceti id ul persoanei: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Introduceti numele persoanei: ");
        String nume = scanner.nextLine();

        System.out.print("Introduceti varsta persoanei: ");
        int varsta = scanner.nextInt();
        scanner.nextLine();


        String sql = "INSERT INTO persoane (nume, varsta) VALUES (?, ?)";

        try {
            rs.moveToInsertRow();
            rs.updateInt("id", id);
            rs.updateString("nume", nume);
            rs.updateInt("varsta",varsta);
            rs.insertRow();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void actualizare(ResultSet rs,int id,int varsta){
        boolean modificat=false;
        try {
            rs.beforeFirst();
            while (rs.next())
                if(rs.getInt("id")==id) {
                    rs.updateInt("varsta", varsta);
                    rs.updateRow();
                    modificat=true;
                    break;
                }if(modificat)
                System.out.println("\nVarsta persoanei "+rs.getString("nume")
                        +" a fost actualizata cu succes!");
            else
                System.out.println("Nu se gaseste nici o persoana cu id-ul specificat");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void stergere(ResultSet rs,int id){
        boolean sters=true;
        try {
            rs.beforeFirst();
            while (rs.next())
                if(rs.getInt("id")==id) {
                    rs.deleteRow();
                    sters=true;
                    break;
                }
            if(sters)
                System.out.println("\nPersoana cu id-ul "+id+" a fost stearsa cu succes!");
            else
                System.out.println("Nu se gaseste nici o persoana cu id-ul specificat");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void afisarePersoaneSiExcursii(Connection connection) {
        try {
            String sql = "SELECT persoane.id, persoane.nume, persoane.varsta, excursii.id_excursie, excursii.destinatia, excursii.anul " +
                    "FROM persoane LEFT JOIN excursii ON persoane.id = excursii.id_persoana";


            try (Statement statement = connection.createStatement();
                 ResultSet rs = statement.executeQuery(sql)) {

                int ultimul = -1;

                while (rs.next()) {
                    int curent = rs.getInt("id");

                    if (curent != ultimul) {

                        System.out.println("\nPersoana: " + rs.getString("nume") +
                                ", Varsta: " + rs.getInt("varsta"));


                        System.out.println("Excursii:");
                       ultimul = curent;
                    }


                    System.out.println("   ID Excursie: " + rs.getInt("id_excursie") +
                            ", Destinatia: " + rs.getString("destinatia") +
                            ", Anul: " + rs.getInt("anul"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/sys";
        String sql = "select * from persoane";
        try {
            Connection connection = DriverManager.getConnection(url, "root", "1234");
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = statement.executeQuery(sql);

            afisare_tabela(rs, "Continut initial");
            adaugare(rs, new Scanner(System.in));
            afisare_tabela(rs, "Dupa adaugare");
            actualizare(rs, 4, 24);
            afisare_tabela(rs, "Dupa modificare");
            stergere(rs, 9);
            afisare_tabela(rs, "Dupa stergere");

            afisarePersoaneSiExcursii(connection);

            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}