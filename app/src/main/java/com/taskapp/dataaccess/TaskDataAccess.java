package com.taskapp.dataaccess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.taskapp.exception.AppException;
import com.taskapp.model.Task;
import com.taskapp.model.User;

public class TaskDataAccess {

    private final String filePath;
    private final String userPath = "app/src/main/resources/users.csv";

    private final UserDataAccess userDataAccess;

    public TaskDataAccess() {
        filePath = "app/src/main/resources/tasks.csv";
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * 
     * @param filePath
     * @param userDataAccess
     */
    public TaskDataAccess(String filePath, UserDataAccess userDataAccess) {
        this.filePath = filePath;
        this.userDataAccess = userDataAccess;
    }

    /**
     * CSVから全てのタスクデータを取得します。
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByCode(int)
     * @return タスクのリスト
     */
    public List<Task> findAll() {
        List<Task> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                int code = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                int status = Integer.parseInt(parts[2].trim());
                int repUserCode = Integer.parseInt(parts[3].trim());
                User repUser = findUserByCode(repUserCode);
                list.add(new Task(code, name, status, repUser));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private User findUserByCode(int code) {
        try (BufferedReader reader = new BufferedReader(new FileReader(userPath))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                int userCode = Integer.parseInt(parts[0].trim());
                if (userCode == code) {
                    return new User(userCode, parts[1].trim(), parts[2].trim(), parts[3].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new User(-1, "不明", "", "");
    }

    /**
     * タスクをCSVに保存します。
     * 
     * @param task 保存するタスク
     */
    public void save(Task task) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            String line = task.getCode() + "," +
                    task.getName() + "," +
                    task.getStatus() + "," +
                    task.getRepUser().getCode();
            writer.newLine();
            writer.write(line);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * コードを基にタスクデータを1件取得します。
     * 
     * @param code 取得するタスクのコード
     * @return 取得したタスク
     */
    public Task findByCode(int code) throws AppException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                int taskCode = Integer.parseInt(parts[0].trim());
                if (taskCode == code) {
                    String name = parts[1].trim();
                    int status = Integer.parseInt(parts[2].trim());
                    int repUserCode = Integer.parseInt(parts[3].trim());

                    UserDataAccess uda = new UserDataAccess();
                    User user = uda.findByCode(repUserCode);

                    return new Task(taskCode, name, status, user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new AppException("存在するタスクコードを入力してください。");
    }

    /**
     * タスクデータを更新します。
     * 
     * @param updateTask 更新するタスク
     */
    public void update(Task updateTask) {
        List<Task> allTasks = findAll();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Code,Name,Status,Rep_User_Code");
            writer.newLine();

            for (Task task : allTasks) {
                if (task.getCode() == updateTask.getCode()) {
                    task = updateTask;
                }

                writer.write(task.getCode() + "," + task.getName() + "," + task.getStatus() + ","
                        + task.getRepUser().getCode());
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * コードを基にタスクデータを削除します。
     * 
     * @param code 削除するタスクのコード
     */
    // public void delete(int code) {
    // try () {

    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }

    /**
     * タスクデータをCSVに書き込むためのフォーマットを作成します。
     * 
     * @param task フォーマットを作成するタスク
     * @return CSVに書き込むためのフォーマット文字列
     */
    // private String createLine(Task task) {
    // }
}