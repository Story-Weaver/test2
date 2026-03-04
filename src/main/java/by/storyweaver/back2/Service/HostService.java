package by.storyweaver.back2.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import by.storyweaver.back2.Something;


@Service
public class HostService {

    public String createVM(Something entity) {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    StringBuilder output = new StringBuilder();


    ProcessBuilder pb = new ProcessBuilder(
            "sudo", "/home/user/git_clone/create.sh",
            entity.getName(),
            String.valueOf(entity.getCors()),
            String.valueOf(entity.getRam()),
            String.valueOf(entity.getRom()),
            entity.getPassword()
    );

    // Объединяем поток ошибок с основным потоком вывода
    pb.redirectErrorStream(true);

    Process process = null;
    try {
        process = pb.start();

        // 2. Читаем вывод скрипта в реальном времени
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                // Можно добавить лог, чтобы видеть прогресс в консоли сервера
                logger.info("Script output: {}", line);
            }
        }

        // 3. Устанавливаем ЖЕСТКИЙ лимит ожидания (например, 5 минут)
        // Если скрипт не уложится, Java убьет его и пойдет дальше
        boolean finished = process.waitFor(5, TimeUnit.MINUTES);

        if (!finished) {
            process.destroyForcibly(); // Принудительно завершаем зависший процесс
            return "Error: Превышено время ожидания (5 минут). Скрипт принудительно остановлен.";
        }

        int exitCode = process.exitValue();
        if (exitCode == 0) {
            return output.toString();
        } else {
            return "Ошибка (код " + exitCode + "):\n" + output.toString();
        }

    } catch (InterruptedException e) {
        // Если поток был прерван извне (например, выключили сервер)
        if (process != null) process.destroy();
        Thread.currentThread().interrupt();
        return "Процесс был прерван: " + e.getMessage();
    } catch (Exception e) {
        logger.error("Ошибка при выполнении скрипта", e);
        return "Исключение: " + e.getMessage();
    }
}


    public boolean startVM(String vmName, int sshPort) {
        try {
            String command = String.format("bash /home/user/git_clone/start.sh %s %d", vmName, sshPort);
            return executeBashScript(command);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean stopVM(String vmName) {
        try {
            String command = String.format("bash /home/user/git_clone/stop.sh %s", vmName);
            return executeBashScript(command);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean rebootVM(String vmName, int sshPort) {
        try {
            String command = String.format("bash /home/user/git_clone/reboot.sh %s %d", vmName, sshPort);
            return executeBashScript(command);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteVM(String vmName) {
        try {
            String command = String.format("bash /home/user/git_clone/delete.sh %s", vmName);
            return executeBashScript(command);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean executeBashScript(String command) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(command);
        int exitCode = process.waitFor();
        return true;
    }
}