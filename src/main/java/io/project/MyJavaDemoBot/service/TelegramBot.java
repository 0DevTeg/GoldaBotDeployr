package io.project.MyJavaDemoBot.service;

import io.project.MyJavaDemoBot.config.BotConfig;
import io.project.MyJavaDemoBot.database.Database;
import io.project.MyJavaDemoBot.database.Table;
import io.project.MyJavaDemoBot.help.DocTypes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@PropertySource("application.properties")

@Component
public class TelegramBot extends TelegramLongPollingBot {

    public String templateCommends = "\uD83D\uDD37  Список команд  \uD83D\uDD37            \n" +
            "\n" +
            "\uD83D\uDD38/start - ✋Начать общение✋\n" +
            "\n" +
            "\uD83D\uDD38/profile - \uD83D\uDC40Посмотреть профиль\uD83D\uDC40\n" +
            "\n" +
            "\uD83D\uDD38/price - \uD83D\uDCB5Цены\uD83D\uDCB5\n" +
            "\n" +
            "\uD83D\uDD38/buygold - \uD83D\uDCD3Купить голду\uD83D\uDED2\n" +
            "\n" +
            "\uD83D\uDD38/deposit - \uD83D\uDCB5Пополнить баланс\uD83D\uDCB3\n" +
            "\n" +
            "\uD83D\uDD38/redeem - \uD83E\uDD11Вывести голду\uD83E\uDD11\n" +
            "\n" +
            "\uD83D\uDD38/menu - \uD83D\uDCD4Список команд бота\uD83D\uDCD4";
    String resourcesPath = "files/";
    final BotConfig config;

    public String db_name = "railway";
    public String db_host = "viaduct.proxy.rlwy.net";
    public String db_port = "38292";
    public String db_user = "root";
    public String db_passw = "-ed6h3gbga1dB24GBHHF5cAAbf145EGh";
    public long adminId = 1990414292;
    User user;
    Database database;
    Table users;
    Table goldapayments;
    Connection db_connection;
    int rub = -1;


    public TelegramBot(BotConfig config) {
        this.config = config;
        database = new Database(db_name, db_host, db_port, db_user, db_passw);
        users = new Table(database, "botusers");
        goldapayments = new Table(database, "goldapayments");
        List<BotCommand> menu = new ArrayList<>();
        menu.add(new BotCommand("/start", "Начать общение"));
        menu.add(new BotCommand("/menu", "Cписок команд бота"));
        menu.add(new BotCommand("/price", "Цены"));
        menu.add(new BotCommand("/deposit", "Пополнить баланс"));
        menu.add(new BotCommand("/profile", "Посмотреть профиль"));
        menu.add(new BotCommand("/redeem", "Вывести голду"));
        menu.add(new BotCommand("/buygold", "Купить голду"));

        try {
            db_connection = DriverManager.getConnection("jdbc:mysql://" + db_host + ":" + db_port + "/" + db_name, db_user, db_passw);
        } catch (SQLException e) {

        }
        user = new User();
        try {
            this.execute(new SetMyCommands(menu, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            System.out.println("exept");
        }

    }

    String formerMessage = "";
    String[] insertionValues = new String[2];
    int count = 0;
    String formerImportantMessage = "";

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {


        if (update.hasMessage()) {

            long chatId = update.getMessage().getChatId();

            if (user.messageCounter == 0) {//Во время старта программы проверка пользователя на наличие в таблице, занесение его в таблицу если информация отсутствует
                System.out.println("yes,mc=0");
                ArrayList<String[]> result = users.selectFast(db_connection, new String[]{"chatid", "firstname", "lastname", "link", "money", "gold"}, new String[]{"chatid"}, new String[]{update.getMessage().getChatId() + ""});

                System.out.println(result);
                if (result.size() != 0) {
                    String[] mas = result.get(0);
                    user.setChatId(Long.parseLong(mas[0]));
                    user.setFirstName(mas[1]);
                    user.setLastName(mas[2]);
                    user.setNickname(mas[3]);
                    user.setMoney(Double.parseDouble(mas[4]));
                    user.setGold(Double.parseDouble(mas[5]));
                    System.out.println(user.toString());
                } else {
                    user.setChatId(chatId);
                    users.insertFast(db_connection, new String[]{"chatid", "firstname", "lastname", "link", "money", "gold"}, new String[]{chatId + "", update.getMessage().getChat().getFirstName(), update.getMessage().getChat().getLastName(), update.getMessage().getChat().getUserName(), "0", "0"});
                    user.setMoney(0);
                    user.setGold(0);
                    user.setFirstName(update.getMessage().getChat().getFirstName());
                    user.setLastName(update.getMessage().getChat().getLastName());
                    user.setNickname(update.getMessage().getChat().getUserName());

                }
            }
            user.messageCounter++;

            if (update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                if (formerMessage.equals("/buygold")) {

                } else if (formerMessage.equals("/redeem")) {
                    if (isInteger(messageText)) {
                        int gld = Integer.parseInt(messageText);
                        if (gld > 9 && gld <= 5000) {
                            if (user.gold >= gld) {
                                String[] skins = new String[]{};
                            } else {
                                sendMessage(user.chatId, "На балансе недостаточно голды");
                            }
                        } else {
                            sendMessage(user.chatId, "Введите сумму от 10 до 5000 голды");
                        }
                    } else {
                        sendMessage(user.chatId, "Некорректное значение");
                    }
                } else if (formerMessage.equals("/deposit")) {

                    if (isInteger(messageText)) {
                        int gld = Integer.parseInt(messageText);
                        if (gld > 9 && gld <= 3000) {
                            sendMessageWithButtons(user.chatId, " \uD83D\uDCB3К оплате: " + gld + " руб\uD83D\uDCCD",
                                    new String[][]{{"\uD83D\uDFE2Оформить заявку на пополнение\uD83D\uDFE2"}, {"\uD83D\uDD34Назад\uD83D\uDD34"}},
                                    new String[][]{{"btn_yespay"}, {"btn_nopay"}});
                            rub = gld;
                        } else {
                            sendMessage(user.chatId, "Введите сумму от 10 до 3000 рублей");
                        }
                    } else {
                        sendMessage(user.chatId, "Некорректное значение");
                    }

                } else {


                    if (messageText.equals("/start")) {
                        sendMessage(user.chatId, "➖          \uD83D\uDD38            ➖            \uD83D\uDD36             ➖          \uD83D\uDD38          ➖\n" +
                                "\n" +
                                "\uD83D\uDC4BПриветствую! \uD83D\uDC47Это магазин голды Standoff2\uD83D\uDC47\n" +
                                "\uD83D\uDD25Здесь ты можешь приобрести голду по очень низким ценам\uD83E\uDD11\n" +
                                "\n" +
                                "➖          \uD83D\uDD38            ➖            \uD83D\uDD36             ➖          \uD83D\uDD38          ➖");
                        sendMessage(user.chatId, templateCommends);
                    } else if (messageText.equals("/menu")) {
                        sendMessage(chatId, templateCommends);
                    } else if (messageText.equals("/buygold")) {

                        sendMessage(user.chatId,"\uD83D\uDE41На балансе 0 руб\uD83D\uDE23\n" +
                                "\uD83D\uDD3BПополните баланс для покупки голды");
                        //formerImportantMessage = "/buygold";
                    } else if (messageText.equals("/profile")) {
                        sendMessage(chatId, "\uD83D\uDD3BПрофиль\uD83D\uDD3B\n" +
                                "\n" +
                                "\uD83D\uDD38Имя\uD83D\uDFE1: " + user.firstName + "   \n" +
                                "\n" +
                                "\uD83D\uDD38Баланс\uD83D\uDCB0: " + user.money + "  \n" +
                                "\n" +
                                "\uD83D\uDD38Голда\uD83E\uDDC8: " + user.gold);
                    } else if (messageText.equals("/redeem")) {

                        sendMessage(chatId, "Введите количество голды, которое хотите вывести (от 10 до 5000)");

                    } else if (messageText.equals("/deposit")) {

                        System.out.println("depoooooooooooooooooooooooooooooo");
                        sendMessage(user.chatId, "\uD83D\uDC47Введите сумму на пополнение баланса\uD83D\uDC47");


                    } else if (messageText.equals("/заявки") && user.getChatId() == adminId) {
                        ArrayList<String[]> res = goldapayments.selectFast(db_connection, new String[]{"chatid", "userlink", "moneysum"}, new String[]{}, new String[]{});
                        if (res.size() != 0)
                            for (int i = 0; i < res.size(); i++) {
                                String mtext = "";
                                for (int j = 0; j < res.get(0).length; j++) {
                                    if (j == 0) {
                                        mtext += "Айди: " + res.get(i)[j] + "\n";
                                    } else if (j == 1) {
                                        mtext += "Телеграм: @" + res.get(i)[j] + "\n";
                                    } else {
                                        mtext += "Желаемая сумма: " + res.get(i)[j];
                                    }
                                }
                                sendMessage(adminId, mtext);
                            }
                        else sendMessage(adminId, "Пусто");
                    }else if (messageText.equals("/price")){
                        sendMessage(user.chatId,"♦️Цены♦️\n" +
                                "\n" +
                                "\uD83D\uDCCD1 голда = 0.6 руб");
                    }

                    else {
                        sendMessage(chatId, "\uD83D\uDE15Я не понимаю вас, " + update.getMessage().getChat().getFirstName() + "\uD83D\uDE15");
                    }


                }


                formerMessage = messageText;

            } else if (update.getMessage().hasPhoto()) {
                System.out.println("photo");
                uploadPhoto(update.getMessage().getPhoto());
            } else if (update.getMessage().hasDocument()) {
                Document recentDocument = update.getMessage().getDocument();

                String result = uploadDocument(recentDocument);
                if (result.equals("error")) {
                    sendMessage(chatId, "Не удалось загрузить документ");
                } else {
                    sendMessage(chatId, "Документ успешно загружен: " + result);
                }

                System.out.println(update.getMessage().getDocument().getMimeType());
            } else if (update.getMessage().hasAudio()) {
                System.out.println(uploadAudio(update.getMessage().getAudio()));
            } else if (update.getMessage().hasVideo()) {
                System.out.println(uploadVideo(update.getMessage().getVideo()));
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            if (callbackData.equals("btn_yespay")) {
                EditMessageText editMessageText = new EditMessageText();

                editMessageText.setChatId(user.chatId);
                editMessageText.setMessageId((int) messageId);

                ArrayList<String[]> val = goldapayments.selectFast(db_connection, new String[]{"chatid"}, new String[]{"chatid"}, new String[]{user.chatId + ""});
                if (val.size() == 0) {
                    if (rub == -1) {
                        editMessageText.setText("Что-то пошло не так. Попробуйте создать заявку на пополнение еще раз");

                    } else {
                        goldapayments.insertFast(db_connection, new String[]{"chatid", "userlink", "moneysum", "goldsum"}, new String[]{user.chatId + "", user.getNickname(), rub + "", ((int) (rub / 0.6)) + ""});
                        editMessageText.setText("✅Заявка на пополнение " + rub + " руб создана✅\n" +
                                "\n" +
                                "\uD83D\uDCCCОтправьте указанную сумму на QIWI ****\n" +
                                "\n" +
                                "\uD83D\uDCCCПосле проверки платежа средства автоматически зачислятся на ваш баланс\n" +
                                "\n" +
                                "❗️Если платежа не поступит в течение 24 часов, заявка отменяется автоматически.");
                        rub = -1;
                    }
                } else {
                    editMessageText.setText("❗️Вы уже создавали заявку на вывод!\n" +
                            "\n" +
                            "\uD83D\uDD38Дождитесь одобрения старой или удалите старую заявку");
                    editMessageText = addButtonsToEditMessageText(editMessageText, new String[][]{{"\uD83D\uDDD1Удалить заявку\uD83D\uDDD1"}}, new String[][]{{"btn_delrequest"}});

                }


                try {
                    this.execute(editMessageText);
                } catch (TelegramApiException e) {
                    System.err.println(e);
                }

            } else if (callbackData.equals("btn_nopay")) {
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(user.chatId);
                editMessageText.setMessageId((int) messageId);
                editMessageText.setText("❌Заявка отменена❌");
                rub = -1;
                try {
                    this.execute(editMessageText);
                } catch (TelegramApiException e) {
                    System.err.println(e);
                }
            } else if (callbackData.equals("btn_delrequest")) {
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(user.chatId);
                editMessageText.setMessageId((int) messageId);
                goldapayments.deleteFast(db_connection, new String[]{}, new String[]{"chatid"}, new String[]{user.chatId + ""});
                editMessageText.setText("\uD83D\uDDD1Все заявки на пополнение удалены\uD83D\uDDD1");
                rub = -1;
                try {
                    this.execute(editMessageText);
                } catch (TelegramApiException e) {
                    System.err.println(e);
                }
            }

        }


    }

    public void sendMessage(long chatId, String text) {
        SendMessage sendMessage = new SendMessage(chatId + "", text);
        try {
            this.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(SendMessage sendMessage) {
        try {
            this.execute(sendMessage);
        } catch (TelegramApiException e) {

        }
    }

    public void sendPhoto(long chatId, String filepath) {
        InputFile inputFile = new InputFile();
        inputFile.setMedia(new java.io.File(filepath));
        SendPhoto sendPhoto = new SendPhoto(chatId + "", inputFile);
        try {
            this.execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessageWithButtons(long chatId, String text, String[][] buttonsName, String[][] buttonsCallbackData) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        List<List<InlineKeyboardButton>> inlineKeyboardButtons = new ArrayList<>();
        for (int i = 0; i < buttonsName.length; i++) {
            List<InlineKeyboardButton> btns = new ArrayList<>();
            for (int j = 0; j < buttonsName[i].length; j++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(buttonsName[i][j]);
                button.setCallbackData(buttonsCallbackData[i][j]);
                btns.add(button);
            }
            inlineKeyboardButtons.add(btns);
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(inlineKeyboardButtons);
        message.setReplyMarkup(inlineKeyboardMarkup);
        try {
            this.execute(message);
        } catch (TelegramApiException e) {

        }
    }

    public SendMessage addButtonsToSendMessage(SendMessage message, String[][] buttonsName, String[][] buttonsCallbackData) {
        List<List<InlineKeyboardButton>> inlineKeyboardButtons = new ArrayList<>();
        for (int i = 0; i < buttonsName.length; i++) {
            List<InlineKeyboardButton> btns = new ArrayList<>();
            for (int j = 0; j < buttonsName[i].length; j++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(buttonsName[i][j]);
                button.setCallbackData(buttonsCallbackData[i][j]);
                btns.add(button);
            }
            inlineKeyboardButtons.add(btns);
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(inlineKeyboardButtons);
        message.setReplyMarkup(inlineKeyboardMarkup);
        try {
            this.execute(message);
        } catch (TelegramApiException e) {

        }
        return message;
    }

    public EditMessageText addButtonsToEditMessageText(EditMessageText message, String[][] buttonsName, String[][] buttonsCallbackData) {
        List<List<InlineKeyboardButton>> inlineKeyboardButtons = new ArrayList<>();
        for (int i = 0; i < buttonsName.length; i++) {
            List<InlineKeyboardButton> btns = new ArrayList<>();
            for (int j = 0; j < buttonsName[i].length; j++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(buttonsName[i][j]);
                button.setCallbackData(buttonsCallbackData[i][j]);
                btns.add(button);
            }
            inlineKeyboardButtons.add(btns);
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(inlineKeyboardButtons);
        message.setReplyMarkup(inlineKeyboardMarkup);
        try {
            this.execute(message);
        } catch (TelegramApiException e) {

        }
        return message;
    }


    public String formatOtstup(String msg, int symbolsAmount) {
        String answer = "";
        for (int j = 0; j < symbolsAmount; j++) {
            answer += "⠀";
        }
        answer += msg;
        for (int j = 0; j < symbolsAmount; j++) {
            answer += "⠀";
        }
        return answer;
    }

    // TODO: 30.12.2023 finish save photo method
    public String uploadPhoto(List<PhotoSize> photos) {
        String finalPhotoFilepath = resourcesPath + "photos/";

        GetFile getFile = new GetFile();
        getFile.setFileId(photos.get(photos.size() - 1).getFileId());
        String doc_name = (int) (Math.random() * 100000) + "ddddd";
        try {
            org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);
            downloadFile(file, new File(finalPhotoFilepath + "_" + doc_name + ".jpg"));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return finalPhotoFilepath;
    }

    public String uploadAudio(Audio audio) {
        String finalAudioFilepath = resourcesPath + "audios/";

        GetFile getFile = new GetFile();
        getFile.setFileId(audio.getFileId());
        String doc_name = audio.getFileName();
        try {
            org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);
            downloadFile(file, new File(finalAudioFilepath + doc_name));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }


        return finalAudioFilepath + doc_name;
    }

    public String uploadVideo(Video video) {
        System.out.println(video.toString());
        String finalVideoPath = resourcesPath + "videos/";
        video.setFileName(generateNameForFile("mp4"));
        GetFile getFile = new GetFile();
        getFile.setFileId(video.getFileId());
        String doc_name = video.getFileName();


        try {
            org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);
            downloadFile(file, new File(finalVideoPath + doc_name));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }


        return finalVideoPath + doc_name;
    }

    public String uploadDocument(Document document) {
        String filepathOfTheSavedDocument = resourcesPath + "docs/";


        GetFile getFile = new GetFile();
        getFile.setFileId(document.getFileId());
        String doc_name = "";
        if (document.getMimeType().equals(DocTypes.DOC.getValue())) {
            doc_name = generateNameForFile("doc");
        } else if (document.getMimeType().equals(DocTypes.DOCX.getValue())) {
            doc_name = generateNameForFile("docx");
        } else if (document.getMimeType().equals(DocTypes.PDF.getValue())) {
            doc_name = generateNameForFile("pdf");
        } else if (document.getMimeType().equals(DocTypes.TXT.getValue())) {
            doc_name = generateNameForFile("txt");
        } else if (document.getMimeType().equals(DocTypes.ZIP.getValue())) {
            doc_name = generateNameForFile("zip");
        } else if (document.getMimeType().equals(DocTypes.RTF.getValue())) {
            doc_name = generateNameForFile("rtf");
        } else if (document.getMimeType().equals(DocTypes.RAR.getValue())) {
            doc_name = generateNameForFile("rar");
        }
        try {
            org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);
            downloadFile(file, new File(filepathOfTheSavedDocument + doc_name));
        } catch (TelegramApiException e) {
            return "error";
        }

        return filepathOfTheSavedDocument + doc_name;
    }

    public boolean isFileInFolder(String pathToFolder, String fileName) {
        File folder = new File(pathToFolder);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if (file.getName().equals(fileName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String generateNameForFile(String type) {

        String filename = "";
        switch (type) {
            case "pdf":
                String name = (int) (Math.random() * 10001000) + ".pdf";
                while (isFileInFolder(resourcesPath + "docs/", name)) {
                    name = (int) (Math.random() * 10001000) + ".pdf";
                }
                filename = name;
                break;
            case "docx":
                String name1 = (int) (Math.random() * 10001000) + ".docx";
                while (isFileInFolder(resourcesPath + "docs/", name1)) {
                    name1 = (int) (Math.random() * 10001000) + ".docx";
                }
                filename = name1;
                break;
            case "doc":
                String name2 = (int) (Math.random() * 10001000) + ".doc";
                while (isFileInFolder(resourcesPath + "docs/", name2)) {
                    name2 = (int) (Math.random() * 10001000) + ".doc";
                }
                filename = name2;
                break;
            case "mp3":
                String name3 = (int) (Math.random() * 10001000) + ".mp3";
                while (isFileInFolder(resourcesPath + "audios/", name3)) {
                    name3 = (int) (Math.random() * 10001000) + (char) (int) (Math.random() * 245) + ".mp3";
                }
                filename = name3;
                break;
            case "mp4":
                String name4 = (int) (Math.random() * 10001000) + ".mp4";
                while (isFileInFolder(resourcesPath + "videos/", name4)) {
                    name4 = (int) (Math.random() * 10001000) + ".mp4";
                }
                filename = name4;
                break;
            case "rtf":
                String name5 = (int) (Math.random() * 10001000) + ".rtf";
                while (isFileInFolder(resourcesPath + "docs/", name5)) {
                    name5 = (int) (Math.random() * 10001000) + ".rtf";
                }
                filename = name5;
                break;
            case "zip":
                String name6 = (int) (Math.random() * 10001000) + ".zip";
                while (isFileInFolder(resourcesPath + "docs/", name6)) {
                    name6 = (int) (Math.random() * 10001000) + ".zip";
                }
                filename = name6;
                break;
            case "rar":
                String name7 = (int) (Math.random() * 10001000) + ".rar";
                while (isFileInFolder(resourcesPath + "docs/", name7)) {
                    name7 = (int) (Math.random() * 10001000) + ".rar";
                }
                filename = name7;
                break;
            case "txt":
                String name8 = (int) (Math.random() * 10001000) + ".txt";
                while (isFileInFolder(resourcesPath + "docs/", name8)) {
                    name8 = (int) (Math.random() * 10001000) + ".txt";
                }
                filename = name8;
                break;

        }
        return filename;
    }

    public boolean isInteger(String s) {
        try {
            int s1 = Integer.parseInt(s);
        } catch (NumberFormatException exception) {
            return false;
        }
        return true;

    }


}