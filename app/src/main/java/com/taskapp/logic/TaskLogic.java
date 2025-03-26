package com.taskapp.logic;

import java.time.LocalDate;
import java.util.List;

import com.taskapp.dataaccess.LogDataAccess;
import com.taskapp.dataaccess.TaskDataAccess;
import com.taskapp.dataaccess.UserDataAccess;
import com.taskapp.exception.AppException;
import com.taskapp.model.Log;
import com.taskapp.model.Task;
import com.taskapp.model.User;

public class TaskLogic {
    private final TaskDataAccess taskDataAccess;
    private final LogDataAccess logDataAccess;
    private final UserDataAccess userDataAccess;

    public TaskLogic() {
        taskDataAccess = new TaskDataAccess();
        logDataAccess = new LogDataAccess();
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * 
     * @param taskDataAccess
     * @param logDataAccess
     * @param userDataAccess
     */
    public TaskLogic(TaskDataAccess taskDataAccess, LogDataAccess logDataAccess, UserDataAccess userDataAccess) {
        this.taskDataAccess = taskDataAccess;
        this.logDataAccess = logDataAccess;
        this.userDataAccess = userDataAccess;
    }

    /**
     * 全てのタスクを表示します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findAll()
     * @param loginUser ログインユーザー
     */
    public void showAll(User loginUser) {
        TaskDataAccess tda = new TaskDataAccess();
        List<Task> tasks = tda.findAll();

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            String statusStr = switch (task.getStatus()) {
                case 0 -> "未着手";
                case 1 -> "着手中";
                case 2 -> "完了";
                default -> "不明";
            };

            String assigned;
            if (task.getRepUser().getCode() == loginUser.getCode()) {
                assigned = "あなたが担当しています";
            } else {
                assigned = task.getRepUser().getName() + "が担当しています";
            }
            System.out.println((i + 1) + ". タスク名: " + task.getName() + ", 担当者名: " + assigned + ", ステータス: " + statusStr);
        }

    }

    /**
     * 新しいタスクを保存します。
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#save(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code        タスクコード
     * @param name        タスク名
     * @param repUserCode 担当ユーザーコード
     * @param loginUser   ログインユーザー
     * @throws AppException ユーザーコードが存在しない場合にスローされます
     */
    public void save(int code, String name, int repUserCode,
            User loginUser) throws AppException {
        UserDataAccess uda = new UserDataAccess();
        User assignedUser = uda.findByCode(repUserCode);

        Task task = new Task(code, name, 0, assignedUser);
        TaskDataAccess tda = new TaskDataAccess();
        tda.save(task);

        Log log = new Log(code, loginUser.getCode(), 0, LocalDate.now());
        LogDataAccess lda = new LogDataAccess();
        lda.save(log);
    }

    /**
     * タスクのステータスを変更します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#update(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code      タスクコード
     * @param status    新しいステータス
     * @param loginUser ログインユーザー
     * @throws AppException タスクコードが存在しない、またはステータスが前のステータスより1つ先でない場合にスローされます
     */
    public void changeStatus(int code, int status,
            User loginUser) throws AppException {
        TaskDataAccess tda = new TaskDataAccess();
        Task task = tda.findByCode(code);

        int currentStatus = task.getStatus();

        if (status != currentStatus + 1) {
            throw new AppException("ステータスは、前のステータスより1つ先のもののみを選択してください");
        }

        task.setStatus(status);
        tda.update(task);

        LogDataAccess lda = new LogDataAccess();
        LocalDate date = LocalDate.now();
        Log log = new Log(code, loginUser.getCode(), status, date);
        lda.save(log);
    }

    /**
     * タスクを削除します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#delete(int)
     * @see com.taskapp.dataaccess.LogDataAccess#deleteByTaskCode(int)
     * @param code タスクコード
     * @throws AppException タスクコードが存在しない、またはタスクのステータスが完了でない場合にスローされます
     */
    // public void delete(int code) throws AppException {
    // }
}