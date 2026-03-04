package by.storyweaver.back2.Service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import by.storyweaver.back2.Something;


@Service
public class HostService {

    public boolean createVM(Something entity) {
        try {
            String command = String.format("bash /home/user/create.sh %s %d %d %d %s",
                    entity.getName(), entity.getCors(), entity.getRam(), entity.getRom(), entity.getPassword());
            return executeBashScript(command);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
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