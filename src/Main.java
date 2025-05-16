import java.util.*;
import java.io.*;
import java.sql.*;

public class Main {
    public static void main(String[] args){

        DataBaseSQL.dataBaseSQL();

        DataBaseTXT.dataBaseTXT();

        String current_role = SignIn.role();

        String current_login = SignIn.login(current_role);

        if (current_login.equals("exit")){
            System.exit(0);
        }

        switch (current_role){
            case "personal":
                Personal.personal();
                break;
            case "director":
                Director.director(current_login);
                break;
            case "manager":
                Manager.manager();
                break;
            case "client":
                Client.client(current_login);
                break;
        }
    }
}

class DataBaseSQL {
    public static HashMap <String, String> HM_login_check = new HashMap<>();

    public static HashMap <String, ArrayList <String> > HM_role_check = new HashMap<>();

    public static HashMap <String, String> HM_password_check = new HashMap<>();

    public static HashMap <String, Integer> HM_name_of_procedure_check = new HashMap<>();

    public static HashMap <String, Integer> HM_login_of_clients_check = new HashMap<>();

    public static final String DB_URL = "jdbc:sqlite:fitness_project.db";

    public static void dataBaseSQL(){

        /*
        dropTable("users");
        dropTable("procedures");
        dropTable("clients");
        */

        createTable();

        if (firstTimeOrNot()) {
            insertDataToUsers("personal", "personal1", "pel1");
            insertDataToUsers("director", "director1", "dir1");
            insertDataToUsers("manager", "manager1", "mar1");
            insertDataToUsers("client", "client1", "clt1");

            insertDataToProcedures("Массаж", 2000.00);
            insertDataToProcedures("Йога", 5000.00);
            insertDataToProcedures("Бассейн", 4000.00);

            insertDataToClients("client1", "Мирлан", "Кыдыев", 182, 75, 3, "14.02.2007");
            insertDataToClients("client2", "Тагайбек", "Кубатов", 200, 150, 2, "20.10.2006");
        }

        readUsersData();
        readProceduresData();
        readClientsData();
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private static void dropTable(String tableName) {
        String sql = "DROP TABLE IF EXISTS " + tableName;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("✅ Таблица '" + tableName + "' успешно удалена (или не существовала).");

        } catch (SQLException e) {
            System.err.println("❌ Ошибка при удалении таблицы: " + e.getMessage());
        }
    }

    private static void createTable() {
        String sqlUsers = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                role TEXT NOT NULL,
                login TEXT NOT NULL,
                password TEXT NOT NULL
            );
            """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlUsers);
            System.out.println("✅ Таблица создана (или уже существует).");
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при создании таблицы: " + e.getMessage());
        }

        String sqlProcedures = """
            CREATE TABLE IF NOT EXISTS procedures (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                cost DOUBLE NOT NULL
            );
            """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlProcedures);
            System.out.println("✅ Таблица создана (или уже существует).");
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при создании таблицы: " + e.getMessage());
        }

        String sqlClients = """
            CREATE TABLE IF NOT EXISTS clients (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                login TEXT NOT NULL,
                name TEXT NOT NULL,
                surname TEXT NOT NULL,
                height INTEGER NOT NULL,
                weight INTEGER NOT NULL,
                bloodType INTEGER NOT NULL,
                dateOfBirth TEXT NOT NULL
           );
            """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlClients);
            System.out.println("✅ Таблица создана (или уже существует).");
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при создании таблицы: " + e.getMessage());
        }
    }

    private static boolean firstTimeOrNot() {
        String sql = "SELECT 1 FROM users LIMIT 1";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return false;
            } else {
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при работе с базой данных: " + e.getMessage());
        }
        return true;
    }

    public static void insertDataToUsers(String role, String login, String password) {
        String sql = "INSERT INTO users(role, login, password) VALUES(?, ?, ?)";

        if (HM_login_check.get(login) == null) {
            try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, role);
                pstmt.setString(2, login);
                pstmt.setString(3, password);

                pstmt.executeUpdate();

                System.out.println("✅ Данные добавлены: " + role + " " + login);
            } catch (SQLException e) {
                System.err.println("❌ Ошибка при вставке данных: " + e.getMessage());
            }
        }
    }

    public static void insertDataToProcedures(String name, Double cost) {
        String sql = "INSERT INTO procedures(name, cost) VALUES(?, ?)";

        if (HM_name_of_procedure_check.get(name) == null) {
            try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setDouble(2, cost);

                pstmt.executeUpdate();

                System.out.println("✅ Данные добавлены: " + name + " " + cost + "0 сом");
            } catch (SQLException e) {
                System.err.println("❌ Ошибка при вставке данных: " + e.getMessage());
            }
        }
    }

    public static void insertDataToClients(String login, String name, String surname, Integer height, Integer weight, Integer bloodType, String dateOfBirth) {
        String sql = "INSERT INTO clients(login, name, surname, height, weight, bloodType, dateOfBirth) VALUES(?, ?, ?, ?, ?, ?, ?)";

        if (HM_login_of_clients_check.get(login) == null) {
            try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, login);
                pstmt.setString(2, name);
                pstmt.setString(3, surname);
                pstmt.setInt(4, height);
                pstmt.setInt(5, weight);
                pstmt.setInt(6, bloodType);
                pstmt.setString(7, dateOfBirth);

                pstmt.executeUpdate();

                System.out.println("✅ Данные добавлены: " + login + " " + name);
            } catch (SQLException e) {
                System.err.println("❌ Ошибка при вставке данных: " + e.getMessage());
            }
        }
    }

    private static void readUsersData() {
        String sql = "SELECT * FROM users";

        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String role = rs.getString("role");
                String login = rs.getString("login");
                String password = rs.getString("password");

                HM_login_check.put(login, role);

                if (HM_role_check.get(role) != null){
                    HM_role_check.get(role).add(login);
                }
                else{
                    ArrayList <String> AL = new ArrayList<>();
                    AL.add(login);

                    HM_role_check.put(role, AL);
                }

                HM_password_check.put(login, password);
            }
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при чтении данных: " + e.getMessage());
        }
    }

    private static void readProceduresData() {
        String sql = "SELECT * FROM procedures";

        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");

                HM_name_of_procedure_check.put(name, id);
            }
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при чтении данных: " + e.getMessage());
        }
    }

    private static void readClientsData() {
        String sql = "SELECT * FROM clients";

        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String login = rs.getString("login");

                HM_login_of_clients_check.put(login, id);
            }
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при чтении данных: " + e.getMessage());
        }
    }

    public static boolean isRight(String role, String login, String password) {
        if (HM_login_check.get(login) == null){
            return false;
        }

        boolean isTrue = false;

        for (String x : HM_role_check.get(role)){
            if (x.equals(login)){
                isTrue = true;
                break;
            }
        }

        if (!isTrue){
            return false;
        }

        return HM_password_check.get(login).equals(password);
    }
}

class DataBaseTXT {
    public static HashMap <String, Boolean> HM_schedule_check = new HashMap<>();

    public static ArrayList <String> proceduresSchedule = new ArrayList<>();

    public static void dataBaseTXT() {

        /* deleteFile(); */

        createFile();

        readFile();

        writeFile("Массаж", "Понедельник", "13:00");
    }

    private static void deleteFile(){
        File file = new File("procedureSchedule.txt");
        if (file.delete()) {
            System.out.println("Файл удален");
        } else {
            System.out.println("Файл не найден");
        }
    }

    private static void createFile(){
        try {
            File file = new File("procedureSchedule.txt");
            if (file.createNewFile()) {
                System.out.println("Файл создан");
            } else {
                System.out.println("Файл уже существует");
            }
        } catch (IOException e) {
            System.out.println("Ошибка при создании файла");
        }
    }

    public static void readFile(){
        try {
            FileReader fileReader = new FileReader("procedureSchedule.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                proceduresSchedule.add(line);
                HM_schedule_check.put(line, true);
            }

            bufferedReader.close();
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла");
        }
    }

    public static void writeFile(String procedureName, String weekDay, String time){
        String newProcedureSchedule = procedureName + " " + weekDay + " " + time;

        if (HM_schedule_check.get(newProcedureSchedule) != null){
            System.out.println("В это время уже стоит процедура");
            return;
        }

        HM_schedule_check.put(newProcedureSchedule, true);

        proceduresSchedule.add(newProcedureSchedule);

        try {
            FileWriter fileWriter = new FileWriter("procedureSchedule.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (String x : proceduresSchedule){
                bufferedWriter.write(x);
                bufferedWriter.newLine();
            }

            bufferedWriter.close();
        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл");
        }
    }
}

class SignIn {
    public static String role(){
        Scanner scan = new Scanner(System.in);

        boolean error = true;
        String role = "";

        while (error) {
            error = false;

            System.out.println();
            System.out.println("personal/director/manager/client");
            System.out.print("Введите тип аккаунта: ");

            role = scan.nextLine();

            switch (role) {
                case "personal": break;
                case "director": break;
                case "manager": break;
                case "client": break;
                default:
                    System.out.println("Ошибка: такого типа аккаунта не существует");
                    error = true;
            }
        }
        return role;
    }

    public static String login(String role){
        Scanner scan = new Scanner(System.in);

        System.out.println();

        System.out.print("Логин: ");
        String login = scan.nextLine();

        System.out.print("Пароль: ");
        String password = scan.nextLine();

        System.out.println();

        if (!DataBaseSQL.isRight(role, login, password)) {
            System.out.println("Логин или пароль введены неправильно.");

            login = "exit";
        }

        return login;
    }
}

class Personal extends DataBaseSQL{
    public static void personal(){
        Scanner scan = new Scanner(System.in);

        System.out.println("Приветствую уважаемый Персонал!");

        boolean running = true;
        boolean anyKey = false;

        while (running) {
            if (anyKey){
                System.out.print("\nНажмите любую клавишу чтобы продолжить. ");
                scan.nextLine();
            }
            anyKey = true;

            System.out.println("\nМеню персонала:");

            System.out.println("1. Показать список процедур");
            System.out.println("2. Найти посетителя");
            System.out.println("3. Показать все процедуры");
            System.out.println("4. Показать расписание к процедурам");
            System.out.println("5. Купить процедуру");
            System.out.println("6. Найти процедуры");
            System.out.println("0. Выход");

            System.out.print("Выбор: ");
            String input = scan.nextLine();
            System.out.println();

            switch (input) {
                case "1":
                    listOfProcedures();
                    break;
                case "2":
                    searchClient();
                    break;
                case "3":
                    allProcedures();
                    break;
                case "4":
                    procedureSchedule();
                    break;
                case "5":
                    buyProcedure();
                    break;
                case "6":
                    findProcedure();
                    break;
                case "0":
                    running = false;
                    System.out.println("Программа завершена, мы будем рады вашему возвращению!");
                    break;
                default:
                    System.out.println("Неверный выбор!");
            }
        }
    }

    public static void listOfProcedures() {

    }

    public static void searchClient(){
        Scanner scan = new Scanner(System.in);

        System.out.print("Введите логин посетителя которого хотите найти: ");
        String login = scan.nextLine();

        String query = "SELECT * FROM clients WHERE login = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, login);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String surname = rs.getString("surname");
                    Integer height = rs.getInt("height");
                    Integer weight = rs.getInt("weight");
                    Integer bloodType = rs.getInt("bloodType");
                    String dateOfBirth = rs.getString("dateOfBirth");

                    System.out.println("Пользователь найден:");
                    System.out.println("ID: " + id);;
                    System.out.println("Имя: " + name);
                    System.out.println("Фамилия: " + surname);
                    System.out.println("Рост: " + height + " см");
                    System.out.println("Вес: " + weight + " кг");
                    System.out.println("Группа крови: " + bloodType);
                    System.out.println("Дата рождения: " + dateOfBirth);

                } else {
                    System.out.println("Пользователь с логином '" + login + "' не найден.");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при поиске посетителя: " + e.getMessage());
        }
    }

    public static void allProcedures(){
        String sql = "SELECT * FROM procedures";

        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("📋 Данные из таблицы:");

            while (rs.next()) {
                System.out.printf("ID: %d | Название: %s | Цена: %d.00 сом | Длительность: %s%n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("price"),
                        rs.getString("time"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при чтении данных: " + e.getMessage());
        }
    }

    public static void procedureSchedule(){

    }

    public static void buyProcedure(){

    }

    public static void findProcedure(){

    }
}

class Director extends DataBaseSQL{
    public static void director(String login){
        Scanner scan = new Scanner(System.in);

        System.out.println("Приветствую дорогой Директор!");
    }
}

class Manager extends DataBaseSQL{
    public static void manager() {
        Scanner scan = new Scanner(System.in);

        System.out.println("Приветствую дорогой Менеджер!");

        boolean running = true;
        boolean anyKey = false;

        while (running) {
            if (anyKey){
                System.out.print("\nНажмите любую клавишу чтобы продолжить. ");
                scan.nextLine();
            }
            anyKey = true;

            System.out.println("\nМеню менеджера:");

            System.out.println("1. Показать список посетителей");
            System.out.println("2. Показать количество посетителей");
            System.out.println("3. Поиск посетителя");
            System.out.println("4. Изменить цену для процедур");
            System.out.println("5. Изменить время - название процедур");
            System.out.println("6. Показать посетителя с максимальным количеством посещений");
            System.out.println("7. Показать посетителя с минимальным количеством посещений");
            System.out.println("0. Выход");

            System.out.print("Выбор: ");
            String input = scan.nextLine();
            System.out.println();

            switch (input) {
                case "1":
                    clientsList();
                    break;
                case "2":
                    clientsCount();
                    break;
                case "3":
                    searchClient();
                    break;
                case "4":
                    changePriceToProcedure();
                    break;
                case "5":
                    changeNameToProcedure();
                    break;
                case "6":
                    maxCountOfVisit();
                    break;
                case "7":
                    minCountOfVisit();
                    break;
                case "0":
                    running = false;
                    System.out.println("Программа завершена, мы будем рады вашему возвращению!");
                    break;
                default:
                    System.out.println("Неверный выбор!");
            }
        }
    }

    public static void clientsList(){
        String sql = "SELECT * FROM clients";

        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("📋 Данные из таблицы:");

            while (rs.next()) {
                System.out.printf("ID: %d | Логин: %s | Имя: %s | Фамилия: %s%n",
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("name"),
                        rs.getString("surname"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при чтении данных: " + e.getMessage());
        }
    }

    public static void clientsCount(){
        String sql = "SELECT COUNT(*) FROM clients";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.print("Количество посетителей: ");
            System.out.println(rs.next() ? rs.getInt(1) : 0);

        } catch (SQLException e) {
            System.err.println("❌ Ошибка при подсчёте строк: " + e.getMessage());
        }
    }

    public static void searchClient(){
        Scanner scan = new Scanner(System.in);

        System.out.print("Введите логин посетителя которого хотите найти: ");
        String login = scan.nextLine();

        String query = "SELECT * FROM clients WHERE login = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, login);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String surname = rs.getString("surname");
                    Integer height = rs.getInt("height");
                    Integer weight = rs.getInt("weight");
                    Integer bloodType = rs.getInt("bloodType");
                    String dateOfBirth = rs.getString("dateOfBirth");

                    System.out.println("Пользователь найден:");
                    System.out.println("ID: " + id);;
                    System.out.println("Имя: " + name);
                    System.out.println("Фамилия: " + surname);
                    System.out.println("Рост: " + height + " см");
                    System.out.println("Вес: " + weight + " кг");
                    System.out.println("Группа крови: " + bloodType);
                    System.out.println("Дата рождения: " + dateOfBirth);

                } else {
                    System.out.println("Пользователь с логином '" + login + "' не найден.");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при поиске посетителя: " + e.getMessage());
        }
    }

    public static void changePriceToProcedure(){
        Scanner scan = new Scanner(System.in);

        System.out.print("Введите название процедуры для изменения её цены: ");
        String name = scan.nextLine();

        if (HM_name_of_procedure_check.get(name) == null){
            System.out.println("\nТакой процедуры не существует.");
            return;
        }

        System.out.print("\nВведите новую цену для этой процедуры: ");
        double newCost = scan.nextDouble();

        String url = "jdbc:sqlite:fitness_project.db";
        String sql = "UPDATE procedures SET cost = ? WHERE name = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, newCost);
            pstmt.setString(2, name);

        } catch (SQLException e) {
            System.out.println("\nОшибка при обновлении стоимости: " + e.getMessage());
        }
    }

    public static void changeNameToProcedure(){
        Scanner scan = new Scanner(System.in);

        System.out.print("Введите название процедуры для изменения её названия: ");
        String name = scan.nextLine();

        if (HM_name_of_procedure_check.get(name) == null){
            System.out.println("\nТакой процедуры не существует.");
            return;
        }

        System.out.print("\nВведите новое название для процедуры: ");
        String newName = scan.nextLine();

        String url = "jdbc:sqlite:fitness_project.db";
        String sql = "UPDATE procedures SET name = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newName);
            pstmt.setInt(2, HM_name_of_procedure_check.get(name));

        } catch (SQLException e) {
            System.out.println("\nОшибка при обновлении названия: " + e.getMessage());
        }
    }

    public static void maxCountOfVisit(){

    }

    public static void minCountOfVisit(){

    }
}

class Client extends DataBaseSQL{
    public static void client(String login){
        Scanner scan = new Scanner(System.in);

        System.out.println("Приветствую дорогой Посетитель!");

        boolean running = true;
        boolean anyKey = false;

        while (running){
            if (anyKey){
                System.out.println();
                System.out.print("Нажмите любую клавишу чтобы продолжить. ");
                scan.nextLine();
            }
            anyKey = true;

            System.out.println("\nМеню клиента:");

            System.out.println("1. Показать историю посещений");
            System.out.println("2. Показать последнюю дату посещения");
            System.out.println("3. Показать историю оплаты");
            System.out.println("4. Показать расписание тренировок");
            System.out.println("5. Показать мою информацию");
            System.out.println("0. Выход");

            System.out.print("Выбор: ");
            String input = scan.nextLine();
            System.out.println();

            switch (input){
                case "1":
                    historyOfVisit();
                    break;
                case "2":
                    theLastDateOfVisit();
                    break;
                case "3":
                    historyOfPayment();
                    break;
                case "4":
                    scheduleOfTraining();
                    break;
                case "5":
                    myInfo();
                    break;
                case "0":
                    running = false;
                    System.out.println("Программа завершена, мы будем рады вашему возвращению!");
                    break;
                default:
                    System.out.println("Неверный выбор!");
            }
        }
    }

    public static void historyOfVisit(){

    }

    public static void theLastDateOfVisit(){

    }

    public static void historyOfPayment(){

    }

    public static void scheduleOfTraining(){

    }

    public static void myInfo(){

    }

}
