package chat.dobot.bot.utils;

public class ConsoleUtil {


    // Códigos ANSI para cores
    public static final String RESET = "\033[0m";  // Resetar a cor
    public static final String RED = "\033[0;31m";     // Cor vermelha
    public static final String GREEN = "\033[0;32m";   // Cor verde
    public static final String YELLOW = "\033[0;33m";  // Cor amarela
    public static final String BLUE = "\033[0;34m";    // Cor azul


    /**
     * Imprime uma mensagem de erro e encerra a aplicação.
     *
     * @param erro a mensagem de erro
     */
    public static void printErro(String erro) {
        System.out.println(RED+"🚨 ERRO:"+RESET+ erro);
    }

    public static void printWarning(String warning) {
        System.out.println(YELLOW+"⚠️ WARNING:"+RESET+ warning);
    }

    public static void printErro(String erro, Exception e) {
        printErro(erro + "\n" + e.getMessage());
    }

    public static void printConsole(String message) {
        System.out.println(message);
    }

    public static void printRed(String message) {
        System.out.println(RED + message + RESET);
    }

    public static void printGreen(String message) {
        System.out.println(GREEN + message + RESET);
    }

    public static void printYellow(String message) {
        System.out.println(YELLOW + message + RESET);
    }

    public static void printBlue(String message) {
        System.out.println(BLUE + message + RESET);
    }


}
