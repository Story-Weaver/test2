package by.storyweaver.back2.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.stereotype.Service;

import by.storyweaver.back2.Something;


@Service
public class HostService {

    public String createVM(Something entity) {
    StringBuilder output = new StringBuilder();

    try {
        String command = String.format("sudo bash ./create.sh %s %d %d %d %s",
                entity.getName(), entity.getCors(), entity.getRam(), entity.getRom(), entity.getPassword());

        ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        int exitCode = process.waitFor();
        if (exitCode == 0) {
            return output.toString();
        } else {
            return "Error: " + exitCode + "\n" + output.toString();
        }
    } catch (IOException | InterruptedException e) {
        e.printStackTrace();
        return "Exception: " + e.getMessage();
    }
    }



    public boolean startVM(String vmName, int sshPort) {
        try {
            String command = String.format("bash /home/user/start.sh %s %d", vmName, sshPort);
            return executeBashScript(command);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean stopVM(String vmName) {
        try {
            String command = String.format("bash /home/user/stop.sh %s", vmName);
            return executeBashScript(command);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean rebootVM(String vmName, int sshPort) {
        try {
            String command = String.format("bash /path/to/reboot_vm.sh %s %d", vmName, sshPort);
            return executeBashScript(command);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteVM(String vmName) {
        try {
            String command = String.format("bash /path/to/delete_vm.sh %s", vmName);
            return executeBashScript(command);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean executeBashScript(String command) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(command);
        int exitCode = process.waitFor();
        return exitCode != 0;
    }
}