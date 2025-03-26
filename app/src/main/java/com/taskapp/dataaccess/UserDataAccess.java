package com.taskapp.dataaccess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.taskapp.exception.AppException;
import com.taskapp.model.User;

public class UserDataAccess {
    private final String filePath;

    public UserDataAccess() {
        filePath = "app/src/main/resources/users.csv";
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * 
     * @param filePath
     */
    public UserDataAccess(String filePath) {
        this.filePath = filePath;
    }

    /**
     * メールアドレスとパスワードを基にユーザーデータを探します。
     * 
     * @param email    メールアドレス
     * @param password パスワード
     * @return 見つかったユーザー
     */
    public User findByEmailAndPassword(String email, String password) throws AppException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 4)
                    continue;

                String code = parts[0].trim();
                String name = parts[1].trim();
                String mail = parts[2].trim();
                String pass = parts[3].trim();

                if (mail.equals(email) && pass.equals(password)) {
                    return new User(Integer.parseInt(code), name, email, pass);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();

        }
        throw new AppException("既に登録されているメールアドレス、パスワードを入力してください");
    }

    /**
     * コードを基にユーザーデータを取得します。
     * 
     * @param code 取得するユーザーのコード
     * @return 見つかったユーザー
     */
    public User findByCode(int code) throws AppException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
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
        throw new AppException("担当者が見つかりません");
    }
}
