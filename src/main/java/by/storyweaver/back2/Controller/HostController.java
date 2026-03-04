package by.storyweaver.back2.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import by.storyweaver.back2.Something; // Убедитесь, что этот импорт присутствует
import by.storyweaver.back2.Service.HostService;

@RestController
@RequestMapping("/api/host")
public class HostController {

    @Autowired
    private HostService hostService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Something entity) {
        try {
            if (!entity.isValid()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            return ResponseEntity.ok().body(hostService.createVM(entity));
           
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("catch" + e.getMessage());
        }
    }

    @PostMapping("/start/{vmName}/{sshPort}")
    public ResponseEntity<?> start(@PathVariable String vmName, @PathVariable int sshPort) {
        try {
            if(vmName == null || vmName.isEmpty() || sshPort == 0){
                return ResponseEntity.badRequest().build();
            }
            if(hostService.startVM(vmName,sshPort)){
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.internalServerError().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/stop/{vmName}")
    public ResponseEntity<?> stop(@PathVariable String vmName) {
        try {
            if(vmName == null || vmName.isEmpty()){
                return ResponseEntity.badRequest().build();
            }
            if(hostService.stopVM(vmName)){
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.internalServerError().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/reboot/{vmName}/{sshPort}")
    public ResponseEntity<?> reboot(@PathVariable String vmName, @PathVariable int sshPort) {
        try {
            if(vmName == null || vmName.isEmpty() || sshPort == 0){
                return ResponseEntity.badRequest().build();
            }
            if(hostService.rebootVM(vmName, sshPort)){
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.internalServerError().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/delete/{vmName}")
    public ResponseEntity<?> delete(@PathVariable String vmName) {
        try {
            if(vmName == null || vmName.isEmpty()){
                return ResponseEntity.badRequest().build();
            }
            if(hostService.deleteVM(vmName)){
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.internalServerError().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}