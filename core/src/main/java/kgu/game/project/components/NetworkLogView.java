package kgu.game.project.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import kgu.game.project.GameResources;
import kgu.game.project.MyGdxGame;


public class NetworkLogView {

    // ───────────────────────────────── поля ──────────────────────────────────
    private final MyGdxGame myGdxGame;
    private final String attackerIp;
    private final Runnable onSubmitPressed;

    private final TextView[] logLines;
    public ImageView background;

    private static final String[] LOG_TEMPLATE = {
        "2024-03-14 08:12:01 [INFO]  Connection from {IP_1}:54231 -> 192.168.1.1:80  GET /index.html  200 OK",
        "2024-03-14 08:12:03 [WARN]  {ATTACKER}:45120 -> 192.168.1.1:22  SYN_FLOOD  DROP",
        "2024-03-14 08:12:05 [INFO]  Connection from {IP_2}:61002 -> 192.168.1.1:443 GET /api/v1/data  200 OK",
        "2024-03-14 08:12:07 [WARN]  {ATTACKER}:45121 -> 192.168.1.1:22  SYN_FLOOD  DROP",
        "2024-03-14 08:12:09 [INFO]  {IP_3}:50321 -> 192.168.1.1:80  POST /login  401 Unauthorized",
        "2024-03-14 08:12:10 [WARN]  {ATTACKER}:45122 -> 192.168.1.1:22  SYN_FLOOD  DROP",
        "2024-03-14 08:12:12 [CRIT]  {ATTACKER}:0     -> 192.168.1.1:0   PORT_SCAN  ports 1-1024  ALERT",
        "2024-03-14 08:12:15 [WARN]  {ATTACKER}:45199 -> 192.168.1.1:21  REFUSED  brute-force hint",
        "2024-03-14 08:12:17 [INFO]  {IP_5}:44000 -> 192.168.1.1:80  GET /favicon.ico  304 Not Modified",
        "2024-03-14 08:12:19 [INFO]  {IP_2}:61100 -> 192.168.1.1:443 GET /api/v2/status  200 OK",
        "2024-03-14 08:12:20 [WARN]  {ATTACKER}:45201 -> 192.168.1.1:22  BRUTE  attempt 14/50",
        "2024-03-14 08:12:21 [INFO]  {IP_6}:55555 -> 192.168.1.1:80  GET /about  200 OK",
        "2024-03-14 08:12:23 [CRIT]  {ATTACKER}:45202 -> 192.168.1.1:443 TLS_HANDSHAKE flood  DROP",
        "2024-03-14 08:12:25 [INFO]  {IP_3}:50400 -> 192.168.1.1:80  GET /news  200 OK",
        "2024-03-14 08:12:26 [WARN]  {ATTACKER}:45203 -> 192.168.1.1:8080 REFUSED  suspicious UA",
        "2024-03-14 08:12:28 [INFO]  {IP_4}:49999 -> 192.168.1.1:443 GET /profile  200 OK",
        "2024-03-14 08:12:29 [CRIT]  Firewall: blocked 47 packets from {ATTACKER} in last 30s  ** HIGH RISK **",
    };

    private static final String[] NOISE_IPS = {
        "10.0.0.5", "10.0.0.23", "172.16.0.8", "172.16.5.100",
        "10.10.1.77", "192.168.2.10", "10.0.1.200"
    };


    public NetworkLogView(MyGdxGame myGdxGame, float x, float y, float width, float height,
                          String attackerIp, Runnable onSubmitPressed) {
        this.myGdxGame = myGdxGame;
        this.attackerIp = attackerIp;
        this.onSubmitPressed = onSubmitPressed;

        background = new ImageView(5, 60, 1200, 650, GameResources.BACKGROUND_WINDOW_IMG_PATH);

        logLines = new TextView[LOG_TEMPLATE.length];
        float lineY = y + height - 120f;
        float lineSpacing = 28f;

        for (int i = 0; i < LOG_TEMPLATE.length; i++) {
            String raw = LOG_TEMPLATE[i];
            String line = raw.replace("{ATTACKER}", attackerIp);
            for (int n = 1; n <= 7; n++) {
                line = line.replace("{IP_" + n + "}", NOISE_IPS[(n - 1) % NOISE_IPS.length]);
            }

            logLines[i] = new TextView(
                myGdxGame.commonPixelLogFontText,
                (int) (x + 20),
                (int) (lineY - i * lineSpacing),
                line
            );
        }
    }

    public void draw(SpriteBatch batch) {
        background.draw(batch);
        for (TextView line : logLines) {
            line.draw(batch);
        }
    }


}
