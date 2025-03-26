package com.taskapp.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.taskapp.dataaccess.UserDataAccess;
import com.taskapp.exception.AppException;
import com.taskapp.logic.TaskLogic;
import com.taskapp.logic.UserLogic;
import com.taskapp.model.User;

public class TaskUI {
    private final BufferedReader reader;

    private final UserLogic userLogic;

    private final TaskLogic taskLogic;

    private User loginUser;

    public TaskUI() {
        reader = new BufferedReader(new InputStreamReader(System.in));
        userLogic = new UserLogic();
        taskLogic = new TaskLogic();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * 
     * @param reader
     * @param userLogic
     * @param taskLogic
     */
    public TaskUI(BufferedReader reader, UserLogic userLogic, TaskLogic taskLogic) {
        this.reader = reader;
        this.userLogic = userLogic;
        this.taskLogic = taskLogic;
    }

    /**
     * メニューを表示し、ユーザーの入力に基づいてアクションを実行します。
     *
     * @see #inputLogin()
     * @see com.taskapp.logic.TaskLogic#showAll(User)
     * @see #selectSubMenu()
     * @see #inputNewInformation()
     */
    public void displayMenu() {
        System.out.println("タスク管理アプリケーションにようこそ!!");
        User user = null;
        while (user == null) {
            try {
                user = inputLogin();
            } catch (AppException e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println("ユーザー名: " + user.getName() + "でログインしました。\n");

        // メインメニュー
        boolean flg = true;
        while (flg) {
            try {
                System.out.println("以下1~3のメニューから好きな選択肢を選んでください。");
                System.out.println("1. タスク一覧, 2. タスク新規登録, 3. ログアウト");
                System.out.print("選択肢：");
                String selectMenu = reader.readLine();

                System.out.println();

                switch (selectMenu) {
                    case "1":
                        TaskLogic logic = new TaskLogic();
                        logic.showAll(user);

                        selectSubMenu(user);
                        break;
                    case "2":
                        try {
                            inputNewInformation(user);
                        } catch (AppException e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case "3":
                        System.out.println("ログアウトしました。");
                        flg = false;
                        break;
                    default:
                        System.out.println("選択肢が誤っています。1~3の中から選択してください。");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }

    /**
     * ユーザーからのログイン情報を受け取り、ログイン処理を行います。
     *
     * @see com.taskapp.logic.UserLogic#login(String, String)
     */
    public User inputLogin() throws AppException {
        try {
            System.out.print("メールアドレスを入力してください: ");
            String email = reader.readLine();

            System.out.print("パスワードを入力してください: ");
            String password = reader.readLine();

            UserLogic logic = new UserLogic();
            return logic.login(email, password);

        } catch (IOException e) {
            e.printStackTrace();
            throw new AppException("入力エラーが発生しました。");
        }

    }

    /**
     * ユーザーからの新規タスク情報を受け取り、新規タスクを登録します。
     *
     * @see #isNumeric(String)
     * @see com.taskapp.logic.TaskLogic#save(int, String, int, User)
     */
    public void inputNewInformation(User loginUser) throws AppException {
        try {
            int taskCode;
            String taskName;
            int repUserCode;

            while (true) {

                System.out.print("タスクコードを入力してください: ");
                String taskCodeInput = reader.readLine();
                if (!isNumeric(taskCodeInput)) {
                    System.out.println("コードは半角の数字で入力してください");
                    continue;
                }
                taskCode = Integer.parseInt(taskCodeInput);

                System.out.print("タスク名を入力してください: ");
                taskName = reader.readLine();
                if (taskName.length() > 10) {
                    System.out.println("タスク名は10文字以内で入力してください");
                    continue;
                }

                System.out.print("担当するユーザーのコードを選択してください： ");
                String userCodeInput = reader.readLine();
                if (!isNumeric(userCodeInput)) {
                    System.out.println("ユーザーのコードは半角の数字で入力してください");
                    continue;
                }
                repUserCode = Integer.parseInt(userCodeInput);

                UserDataAccess uda = new UserDataAccess();
                try {
                    uda.findByCode(repUserCode);
                } catch (AppException e) {
                    System.out.println("存在するユーザーコードを入力してください");
                    continue;
                }

                break;

            }

            TaskLogic logic = new TaskLogic();
            logic.save(taskCode, taskName, repUserCode, loginUser);

            System.out.println(taskName + "の登録が完了しました。");

        } catch (IOException e) {
            e.printStackTrace();
            throw new AppException("入力エラーが発生しました。");
        }
    }

    public boolean isNumeric(String str) {
        if (str == null || str.isEmpty())
            return false;
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * タスクのステータス変更または削除を選択するサブメニューを表示します。
     *
     * @see #inputChangeInformation()
     * @see #inputDeleteInformation()
     */
    public void selectSubMenu(User loginUser) {
        try {
            System.out.println("以下1~2から好きな選択肢を選んでください。");
            System.out.println("1. タスクのステータス変更, 2. メインメニューに戻る");
            System.out.print("選択肢：");
            String input = reader.readLine().trim();

            if (!isNumeric(input)) {
                throw new AppException("選択肢は半角の数字で入力してください");
            }

            int choice = Integer.parseInt(input);

            switch (choice) {
                case 1:
                    inputChangeInformation(loginUser);
                    break;
                case 2:
                    return;
                default:
                    throw new AppException("ステータスは1・2の中から選択してください");

            }
        } catch (AppException e) {
            System.out.println(e.getMessage());
            selectSubMenu(loginUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ユーザーからのタスクステータス変更情報を受け取り、タスクのステータスを変更します。
     *
     * @see #isNumeric(String)
     * @see com.taskapp.logic.TaskLogic#changeStatus(int, int, User)
     */
    public void inputChangeInformation(User loginUser) {
        try {
            System.out.print("ステータスを変更するタスクコードを入力してください: ");
            String taskCodeStr = reader.readLine();
            if (!isNumeric(taskCodeStr)) {
                throw new AppException("コードは半角の数字で入力してください");
            }
            int taskCode = Integer.parseInt(taskCodeStr);

            System.out.println("どのステータスに変更するか選択してください。");
            System.out.println("1. 着手中, 2. 完了");
            System.out.print("選択肢： ");
            String statusStr = reader.readLine();

            if (!isNumeric(statusStr)) {
                throw new AppException("ステータスは半角の数字で入力してください");
            }

            int status = Integer.parseInt(statusStr);
            if (status != 1 && status != 2) {
                throw new AppException("ステータスは1・2の中から選択してください");
            }

            TaskLogic logic = new TaskLogic();
            logic.changeStatus(taskCode, status, loginUser);

            System.out.println("ステータスの変更が完了しました。");
        } catch (AppException e) {
            System.out.println(e.getMessage());
            inputChangeInformation(loginUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ユーザーからのタスク削除情報を受け取り、タスクを削除します。
     *
     * @see #isNumeric(String)
     * @see com.taskapp.logic.TaskLogic#delete(int)
     */
    // public void inputDeleteInformation() {
    // }

    /**
     * 指定された文字列が数値であるかどうかを判定します。
     * 負の数は判定対象外とする。
     *
     * @param inputText 判定する文字列
     * @return 数値であればtrue、そうでなければfalse
     */
    // public boolean isNumeric(String inputText) {
    // return false;
    // }
}
