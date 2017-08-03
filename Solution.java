import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/* 
Генератор паролей
*/
public class Solution {


    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("В параметрах запуска необходимо указать длину пароля");
            System.exit(0);
        }

        try {
            int iterator = 0; //счетчик на всякий случай если вдруг закончатся пароли :)
            int passwordLength = Integer.parseInt(args[0]);//задаем длину пароль из параметра

            Path file = Paths.get(String.format("List_of_passwords-%d.txt", passwordLength));//создаем файл (в названии длина пароля)

            ArrayList<String> existsPasswords = null;//список для хранения паролей из файла с текущей длиной пароля
            if (Files.exists(file)) {
                existsPasswords = new ArrayList<>(Files.readAllLines(file));//записываем в список все пароли из файла
            }

            ByteArrayOutputStream password = null;
            if (existsPasswords != null) {//если файл есть и список заполнен
                while (true) {
                    password = getPassword(passwordLength); //получаем новый пароль
                    if (!existsPasswords.contains(password.toString())) {
                        break; //если такого еще нет в файле, то выходим из цикла
                    }
                    else {
                        iterator++;
                        if (iterator > 20) { //если после 20 раз подряд сгенерировались пароли, которые уже есть в файле
                            System.out.format("Похоже в файле %s есть все варианты для данной длины пароля%s", file.getFileName().toString(), System.lineSeparator());
                            System.exit(0);
                        }
                    }
                }
            } else //если файл новый и список с существующими паролями пуст
                password = getPassword(passwordLength);


            //дозаписываем в конец файла новый пароль
            RandomAccessFile raf = new RandomAccessFile(file.toAbsolutePath().toString(), "rw");
            raf.seek(raf.length());
            raf.write(password.toByteArray());
            raf.writeBytes(System.lineSeparator());
            raf.close();

            System.out.println(password);
            System.out.format("Пароль записан в конец файла %s%s", file.getFileName().toString(), System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    public static ByteArrayOutputStream getPassword(int passwordLength) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StringBuilder sb = new StringBuilder();
        int countLowerCase = 0;
        int countUpperCase = 0;
        int countDigit = 0;

        while (countLowerCase == 0 || countUpperCase == 0 || countDigit == 0) {
            int countLetter[] = generateToStringBuilder(sb, passwordLength);

            countDigit = countLetter[0];
            countLowerCase = countLetter[1];
            countUpperCase = countLetter[2];

            if (countLowerCase == 0 || countUpperCase == 0 || countDigit == 0)
                sb.delete(0, sb.length());
        }

        try {
            baos.write(sb.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos;
    }





    /**
     * Добавление сгенерированного пароля в StringBuilder
     *
     * @param sb             ссылка на существующий StringBuilder, для записи в него
     * @param passwordLength длина пароля
     * @return массив из 3 цифр:
     * int[0] - количество цифр в пароле
     * int[1] - количество букв в нижнем регистре в пароле
     * int[2] - количество букв в верхнем регистре в пароле
     */
    private static int[] generateToStringBuilder(StringBuilder sb, int passwordLength) {
        int countDigit = 0;
        int countLowerCase = 0;
        int countUpperCase = 0;
        while (sb.length() < passwordLength) {
            int i = getRandomInt(48, 122);//48-122 - это диапазон символов, в котором встречаются все цифры и все латинские буквы

            if (Character.isLetterOrDigit(i)) {
                if (Character.isDigit(i))
                    countDigit++;
                if (Character.isLowerCase(i))
                    countLowerCase++;
                if (Character.isUpperCase(i))
                    countUpperCase++;

                sb.append((char) i);
            }
        }
        return new int[]{countDigit, countLowerCase, countUpperCase};
    }





    /**
     * Получение случайного числа в заданном диапазоне
     *
     * @param min нижняя граница диапазона
     * @param max верхняя граница диапазона
     * @return случайного число в диапазоне от min до max (включительно)
     */
    private static int getRandomInt(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

}