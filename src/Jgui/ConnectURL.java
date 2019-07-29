package Jgui;

import com.sun.source.tree.WhileLoopTree;
import main.java.board.Board;

import java.sql.*;

public class ConnectURL {
    public static void main(String[] args) {


    }
    public static String sqlBlackMove(Board board) {
        String move = "";
        String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=Chess;" +
                "username=hdz;password=123456";

        ResultSet resultSet = null;

//            try {
//                //SQLServerDriver myDriver = new SQLServerDriver();
//
//                //Connection connection = myDriver.connect();//
////                Connection connection =  DriverManager.getConnection(connectionUrl);
////                System.out.println("Pass Connection");
////                Statement statement = connection.createStatement();
//
//                // Create and execute a SELECT SQL statement.
//                String selectSql = "Declare @T Table ( \"FEN\" varchar(80), \"White Elo\" varchar(50), \"Black Elo\" " +
//                        "varchar(50), \"Move\" varchar(50) ) Declare @X Table ( \"FEN\" varchar(80), \"White Elo\" " +
//                        "varchar(50), \"Black Elo\" varchar(50), \"Move\" varchar(50) ) Insert @T Exec Chess.dbo" +
//                        ".ReturnNextMove '"+chessBoard.toFen()+"' Insert @X Select Top 1 * from @T Select [White Elo] " +
//                        "from @X";
//                resultSet = statement.executeQuery(selectSql);
//
//                // Print results from select statement
//                while (resultSet.next()) {
//                    System.out.println(resultSet.toString());
//                }
//            }
//            catch (SQLException e) {
//                e.printStackTrace();
//            }
        try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement();) {
//            ResultSet rs = stmt.executeQuery("Declare @T Table ( FEN varchar(80), WhiteElo varchar(50), BlackElo varchar(50), Move varchar(50) ) Insert @T Exec Chess.dbo.ReturnNextMove 'rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR' Select * from @T");
//            ResultSetMetaData rsmd = rs.getMetaData();
//            System.out.println("querying SELECT * FROM XXX");
//            int columnsNumber = rsmd.getColumnCount();
//            while (rs.next()) {
//                for (int i = 1; i <= columnsNumber; i++) {
//                    if (i > 1) System.out.print(",  ");
//                    String columnValue = rs.getString(i);
//                    System.out.print(columnValue + " " + rsmd.getColumnName(i));
//                }
//                System.out.println("");
//            }

            String SQL = "Declare @T Table ( FEN varchar(80), WhiteElo varchar(50), BlackElo varchar(50), Move varchar(50) ) Declare @X Table ( FEN varchar(80), WhiteElo varchar(50), BlackElo varchar(50), Move varchar(50) ) Insert @T Exec ReturnNextMove 'rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR' Insert @X Select Top 1 * from @T Select [WhiteElo] from @X";
            String SQL1 = "Declare @T Table ( FEN varchar(80), WhiteElo varchar(50), BlackElo varchar(50), Move " +
                    "varchar(50) ); Declare @X Table ( FEN varchar(80), WhiteElo varchar(50), BlackElo varchar(50), " +
                    "Move varchar(50) ); Insert @T Exec Chess.dbo.ReturnNextMove '"+board.toFen()+"'; Insert @X Select Top 1" +
                    " * from @T; Select [WhiteElo] from @X";
            String SQLtest = "select top 1 * from Chess.dbo.BlackGames2011";
            ResultSet rs = stmt.executeQuery(SQL1);

            while (rs.next()){
                move = (String) rs.getObject(1);
                System.out.println((String) rs.getObject(1));
            }
            rs.close();

            //System.out.println(rs.getString(1));
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
        }
        return move;
    }public static String sqlWhiteMove(Board board) {
        String move = "";
        String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=Chess;" +
                "username=hdz;password=123456";

        ResultSet resultSet = null;

//            try {
//                //SQLServerDriver myDriver = new SQLServerDriver();
//
//                //Connection connection = myDriver.connect();//
////                Connection connection =  DriverManager.getConnection(connectionUrl);
////                System.out.println("Pass Connection");
////                Statement statement = connection.createStatement();
//
//                // Create and execute a SELECT SQL statement.
//                String selectSql = "Declare @T Table ( \"FEN\" varchar(80), \"White Elo\" varchar(50), \"Black Elo\" " +
//                        "varchar(50), \"Move\" varchar(50) ) Declare @X Table ( \"FEN\" varchar(80), \"White Elo\" " +
//                        "varchar(50), \"Black Elo\" varchar(50), \"Move\" varchar(50) ) Insert @T Exec Chess.dbo" +
//                        ".ReturnNextMove '"+chessBoard.toFen()+"' Insert @X Select Top 1 * from @T Select [White Elo] " +
//                        "from @X";
//                resultSet = statement.executeQuery(selectSql);
//
//                // Print results from select statement
//                while (resultSet.next()) {
//                    System.out.println(resultSet.toString());
//                }
//            }
//            catch (SQLException e) {
//                e.printStackTrace();
//            }
        try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement();) {
//            ResultSet rs = stmt.executeQuery("Declare @T Table ( FEN varchar(80), WhiteElo varchar(50), BlackElo varchar(50), Move varchar(50) ) Insert @T Exec Chess.dbo.ReturnNextMove 'rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR' Select * from @T");
//            ResultSetMetaData rsmd = rs.getMetaData();
//            System.out.println("querying SELECT * FROM XXX");
//            int columnsNumber = rsmd.getColumnCount();
//            while (rs.next()) {
//                for (int i = 1; i <= columnsNumber; i++) {
//                    if (i > 1) System.out.print(",  ");
//                    String columnValue = rs.getString(i);
//                    System.out.print(columnValue + " " + rsmd.getColumnName(i));
//                }
//                System.out.println("");
//            }

            String WhiteSQL = "Declare @T Table ( FEN varchar(80), WhiteElo varchar(50), BlackElo varchar(50), Move " +
                    "varchar(50) ); Declare @X Table ( FEN varchar(80), WhiteElo varchar(50), BlackElo varchar(50), " +
                    "Move varchar(50) ); Insert @T Exec Chess.dbo.ReturnNextWhiteMove '"+board.toFen()+"'; Insert @X " +
                    "Select Top 1 * from @T; Select [WhiteElo] from @X";
            String SQLtest = "select top 1 * from Chess.dbo.BlackGames2011";
            ResultSet rs = stmt.executeQuery(WhiteSQL);

            while (rs.next()){
                move = (String) rs.getObject(1);
                System.out.println((String) rs.getObject(1));
            }
            rs.close();

            //System.out.println(rs.getString(1));
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
        }
        return move;
    }

}