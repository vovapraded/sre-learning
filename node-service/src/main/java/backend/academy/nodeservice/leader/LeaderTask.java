package backend.academy.nodeservice.leader;


import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.integration.leader.event.OnGrantedEvent;
import org.springframework.integration.leader.event.OnRevokedEvent;

@Component
public class LeaderTask {

    @EventListener
    public void onGranted(OnGrantedEvent event) {
        System.out.println("🔥 I am the leader!");
        // Запуск фоновой задачи, синхронизации и т.п.
    }

    @EventListener
    public void onRevoked(OnRevokedEvent event) {
        System.out.println("❌ Leadership lost.");
        // Остановить процессы, если нужно
    }
}
