package by.storyweaver.back2.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import by.storyweaver.back2.Something;


@Service
public class HostService {

    public CompletableFuture<String> createVM(Something entity) {
        return CompletableFuture.supplyAsync(() -> {
            StringBuilder output = new StringBuilder();
            
        
            ProcessBuilder pb = new ProcessBuilder("/home/user/git_clone/create.sh", 
                entity.getName(), 
                String.valueOf(entity.getCors()), 
                String.valueOf(entity.getRam()), 
                String.valueOf(entity.getRom()), 
                entity.getPassword()
            );
            
            pb.redirectErrorStream(true);

            try {
                Process process = pb.start();

                // 2. Читаем поток вывода
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                }

                // 3. Ждем завершения с жестким таймаутом (например, 2 минуты)
                boolean finished = process.waitFor(2, TimeUnit.MINUTES);

                if (!finished) {
                    process.destroyForcibly();
                    return "Error: Script execution timeout (2 min exceeded)";
                }

                int exitCode = process.exitValue();
                if (exitCode == 0) {
                    return "Success:\n" + output.toString();
                } else {
                    return "Error Code: " + exitCode + "\nLog: " + output.toString();
                }

            } catch (Exception e) {
                Thread.currentThread().interrupt();
                return "Exception occurred: " + e.getMessage();
            }
        });
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