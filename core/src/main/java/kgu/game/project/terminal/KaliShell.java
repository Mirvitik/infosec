package kgu.game.project.terminal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;

import kgu.game.project.managers.LocalizationManager;


public class KaliShell {

    public static final String FLAG = "game{sud0_c4t_fl4g}";

    public static final String USER = "kali";
    public static final String HOST = "kali";

    public static final String COLOR_PROMPT = "#4E8CFF";
    public static final String COLOR_PATH = "#FFFFFF";
    private static final String COLOR_DIRECTORY = "#5C9DFF";
    private static final String COLOR_EXECUTABLE = "#5FD75F";
    private static final String COLOR_MATCH = "#FF5555";
    private static final String COLOR_GREP_FILE = "#AF5FAF";
    private static final String COLOR_GREP_SEPARATOR = "#00AFAF";

    private static final String KERNEL_RELEASE = "6.12.25-amd64";
    private static final String KERNEL_VERSION = "#1 SMP PREEMPT_DYNAMIC Kali 6.12.25-1kali1 (2025-05-02)";

    private static final String[] COMMANDS = {
        "cat", "cd", "clear", "cp", "date", "echo", "exit", "grep", "help", "history",
        "hostname", "id", "ifconfig", "ls", "mkdir", "mv", "pwd", "rm", "rmdir",
        "sudo", "touch", "uname", "whoami"
    };
    private static final Set<String> COMMAND_SET = new HashSet<>(Arrays.asList(COMMANDS));
    private static final Set<String> BUILTINS = new HashSet<>(Arrays.asList("cd", "exit", "help", "history"));

    private static final String[] HELP = {
        LocalizationManager.get("help.header"),
        LocalizationManager.get("help.ls"),
        LocalizationManager.get("help.cd"),
        LocalizationManager.get("help.pwd"),
        LocalizationManager.get("help.cat"),
        LocalizationManager.get("help.echo"),
        LocalizationManager.get("help.touch"),
        LocalizationManager.get("help.mkdir"),
        LocalizationManager.get("help.cp"),
        LocalizationManager.get("help.mv"),
        LocalizationManager.get("help.rm"),
        LocalizationManager.get("help.rmdir"),
        LocalizationManager.get("help.grep"),
        LocalizationManager.get("help.whoami"),
        LocalizationManager.get("help.uname"),
        LocalizationManager.get("help.ifconfig"),
        LocalizationManager.get("help.date"),
        LocalizationManager.get("help.sudo"),
        LocalizationManager.get("help.history"),
        LocalizationManager.get("help.clear"),
        LocalizationManager.get("help.exit"),
        LocalizationManager.get("help.tab")
    };

    private static class Node {
        String name;
        final boolean isDirectory;
        final String owner;
        final TreeMap<String, Node> children = new TreeMap<>();
        Node parent;
        boolean rootOnly;
        boolean executable;
        String content = "";
        long modified = System.currentTimeMillis();

        Node(String name, boolean isDirectory, String owner) {
            this.name = name;
            this.isDirectory = isDirectory;
            this.owner = owner;
        }

        String path() {
            if (parent == null) return "/";
            String parentPath = parent.path();
            return parentPath.equals("/") ? "/" + name : parentPath + "/" + name;
        }

        boolean isInside(Node ancestor) {
            for (Node node = this; node != null; node = node.parent) {
                if (node == ancestor) return true;
            }
            return false;
        }
    }

    private static class Line {
        final String markup;
        final boolean error;

        Line(String markup, boolean error) {
            this.markup = markup;
            this.error = error;
        }
    }

    private static class Token {
        final String text;
        final boolean operator;

        Token(String text, boolean operator) {
            this.text = text;
            this.operator = operator;
        }
    }

    private static class Placement {
        final Node parent;
        final String name;

        Placement(Node parent, String name) {
            this.parent = parent;
            this.name = name;
        }
    }

    private Node rootDirectory;
    private Node home;
    private Node tmp;
    private Node flagFile;
    private Node cwd;
    private Node previousDirectory;

    private final ArrayList<String> history = new ArrayList<>();
    private ArrayList<Line> output = new ArrayList<>();
    private boolean asRoot;
    private boolean flagRead;
    private boolean clearRequested;
    private boolean exitRequested;
    private int columns = 80;

    public KaliShell() {
        buildFileSystem();
        cwd = home;
        previousDirectory = home;
    }

    public void setColumns(int columns) {
        this.columns = Math.max(20, columns);
    }

    public List<String> execute(String commandLine) {
        output = new ArrayList<>();
        String trimmed = commandLine.trim();
        if (!trimmed.isEmpty()) {
            history.add(trimmed);
            runLine(trimmed);
        }
        List<String> result = new ArrayList<>();
        for (Line line : output) {
            result.add(line.markup);
        }
        return result;
    }

    public boolean isFlagRead() {
        return flagRead;
    }

    public boolean consumeClearRequest() {
        boolean requested = clearRequested;
        clearRequested = false;
        return requested;
    }

    public boolean consumeExitRequest() {
        boolean requested = exitRequested;
        exitRequested = false;
        return requested;
    }

    public List<String> getHistory() {
        return history;
    }

    public String getPromptHeader() {
        return color(COLOR_PROMPT, "┌──(" + USER + "@" + HOST + ")-[")
            + color(COLOR_PATH, displayPath())
            + color(COLOR_PROMPT, "]");
    }

    public String getPromptPrefix() {
        return color(COLOR_PROMPT, "└─$") + " ";
    }

    public String getWindowTitle() {
        return USER + "@" + HOST + ": " + displayPath();
    }

    public String complete(String input) {
        int tokenStart = input.lastIndexOf(' ') + 1;
        String token = input.substring(tokenStart);
        boolean isFirstToken = input.substring(0, tokenStart).trim().isEmpty();

        List<String> candidates = new ArrayList<>();
        String directoryPart = "";
        if (isFirstToken && token.indexOf('/') < 0) {
            for (String command : COMMANDS) {
                if (command.startsWith(token)) candidates.add(command + " ");
            }
        } else {
            int slash = token.lastIndexOf('/');
            directoryPart = token.substring(0, slash + 1);
            String namePrefix = token.substring(slash + 1);
            Node directory = directoryPart.isEmpty() ? cwd : resolve(directoryPart);
            if (directory == null || !directory.isDirectory || !canRead(directory)) return input;
            for (Node child : directory.children.values()) {
                boolean hidden = child.name.startsWith(".") && !namePrefix.startsWith(".");
                if (child.name.startsWith(namePrefix) && !hidden) {
                    candidates.add(child.name + (child.isDirectory ? "/" : " "));
                }
            }
        }
        if (candidates.isEmpty()) return input;
        String completion = candidates.size() == 1 ? candidates.get(0) : commonPrefix(candidates);
        return input.substring(0, tokenStart) + directoryPart + completion;
    }

    public static String escape(String text) {
        return text.replace("[", "[[");
    }

    public static String color(String hex, String text) {
        return "[" + hex + "]" + escape(text) + "[]";
    }

    public static String stripMarkup(String markup) {
        StringBuilder plain = new StringBuilder();
        for (int i = 0; i < markup.length(); i++) {
            char c = markup.charAt(i);
            if (c != '[') {
                plain.append(c);
            } else if (i + 1 < markup.length() && markup.charAt(i + 1) == '[') {
                plain.append('[');
                i++;
            } else {
                int end = markup.indexOf(']', i);
                if (end < 0) break;
                i = end;
            }
        }
        return plain.toString();
    }


    public static List<String> wrap(String markup, int columns) {
        List<String> result = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        String openTag = null;
        int visible = 0;
        int i = 0;
        while (i < markup.length()) {
            char c = markup.charAt(i);
            String piece;
            if (c == '[' && i + 1 < markup.length() && markup.charAt(i + 1) == '[') {
                piece = "[[";
                i += 2;
            } else if (c == '[' && markup.indexOf(']', i) > 0) {
                int end = markup.indexOf(']', i);
                String tag = markup.substring(i, end + 1);
                openTag = tag.equals("[]") ? null : tag;
                line.append(tag);
                i = end + 1;
                continue;
            } else {
                piece = String.valueOf(c);
                i++;
            }
            if (visible == columns) {
                if (openTag != null) line.append("[]");
                result.add(line.toString());
                line.setLength(0);
                if (openTag != null) line.append(openTag);
                visible = 0;
            }
            line.append(piece);
            visible++;
        }
        result.add(line.toString());
        return result;
    }

    private void runLine(String commandLine) {
        List<Token> tokens = tokenize(commandLine);
        if (tokens == null) {
            error("zsh: unmatched quote");
            return;
        }

        String redirectTarget = null;
        boolean append = false;
        List<String> words = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            if (!token.operator) {
                words.add(token.text);
                continue;
            }
            if (i + 1 >= tokens.size() || tokens.get(i + 1).operator) {
                error("zsh: parse error near `\\n'");
                return;
            }
            redirectTarget = tokens.get(++i).text;
            append = token.text.equals(">>");
        }

        int start = output.size();
        if (!words.isEmpty()) {
            runCommand(words.get(0), words.subList(1, words.size()));
        }
        if (redirectTarget == null) return;

        List<Line> produced = new ArrayList<>(output.subList(start, output.size()));
        output.subList(start, output.size()).clear();
        StringBuilder text = new StringBuilder();
        for (Line line : produced) {
            if (line.error) {
                output.add(line);
            } else {
                text.append(stripMarkup(line.markup)).append('\n');
            }
        }
        writeFile(redirectTarget, text.toString(), append);
    }

    private static List<Token> tokenize(String line) {
        List<Token> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inToken = false;
        char quote = 0;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (quote != 0) {
                if (c == quote) {
                    quote = 0;
                } else {
                    current.append(c);
                }
            } else if (c == '"' || c == '\'') {
                quote = c;
                inToken = true;
            } else if (Character.isWhitespace(c) || c == '>') {
                if (inToken) {
                    tokens.add(new Token(current.toString(), false));
                    current.setLength(0);
                    inToken = false;
                }
                if (c == '>') {
                    boolean isAppend = i + 1 < line.length() && line.charAt(i + 1) == '>';
                    if (isAppend) i++;
                    tokens.add(new Token(isAppend ? ">>" : ">", true));
                }
            } else {
                current.append(c);
                inToken = true;
            }
        }
        if (quote != 0) return null;
        if (inToken) tokens.add(new Token(current.toString(), false));
        return tokens;
    }

    private void runCommand(String name, List<String> args) {
        switch (name) {
            case "help":
                for (String line : HELP) print(line);
                break;
            case "ls":
                ls(args);
                break;
            case "cd":
                cd(args);
                break;
            case "pwd":
                print(cwd.path());
                break;
            case "cat":
                cat(args);
                break;
            case "echo":
                print(String.join(" ", args));
                break;
            case "touch":
                touch(args);
                break;
            case "mkdir":
                mkdir(args);
                break;
            case "cp":
                cp(args);
                break;
            case "mv":
                mv(args);
                break;
            case "rm":
                rm(args);
                break;
            case "rmdir":
                rmdir(args);
                break;
            case "grep":
                grep(args);
                break;
            case "whoami":
                print(asRoot ? "root" : USER);
                break;
            case "id":
                print(asRoot
                    ? "uid=0(root) gid=0(root) groups=0(root)"
                    : "uid=1000(kali) gid=1000(kali) groups=1000(kali),4(adm),20(dialout),24(cdrom),"
                    + "25(floppy),27(sudo),29(audio),30(dip),44(video),46(plugdev),100(users),106(netdev),"
                    + "118(wireshark),121(bluetooth),134(scanner),141(kaboxer)");
                break;
            case "hostname":
                print(HOST);
                break;
            case "uname":
                uname(args);
                break;
            case "date":
                print(new SimpleDateFormat("EEE MMM d hh:mm:ss a zzz yyyy", Locale.US).format(new Date()));
                break;
            case "ifconfig":
                ifconfig();
                break;
            case "history":
                for (int i = 0; i < history.size(); i++) {
                    print(String.format(Locale.US, "%5d  %s", i + 1, history.get(i)));
                }
                break;
            case "sudo":
                sudo(args);
                break;
            case "clear":
                clearRequested = true;
                break;
            case "exit":
                exitRequested = true;
                break;
            default:
                error("zsh: command not found: " + name);
        }
    }

    // реализация основных команд из терминала Linux
    private void ls(List<String> args) {
        List<String> paths = new ArrayList<>();
        String flags = flags(args, paths);
        boolean showAll = flags.indexOf('a') >= 0;
        boolean longFormat = flags.indexOf('l') >= 0;
        if (paths.isEmpty()) paths.add(".");

        for (int i = 0; i < paths.size(); i++) {
            String path = paths.get(i);
            Node node = resolve(path);
            if (node == null) {
                error("ls: cannot access '" + path + "': No such file or directory");
                continue;
            }
            List<String> names = new ArrayList<>();
            List<Node> nodes = new ArrayList<>();
            if (!node.isDirectory) {
                names.add(path);
                nodes.add(node);
            } else if (!canRead(node)) {
                error("ls: cannot open directory '" + path + "': Permission denied");
                continue;
            } else {
                if (paths.size() > 1) {
                    if (i > 0) print("");
                    print(path + ":");
                }
                if (showAll) {
                    names.add(".");
                    nodes.add(node);
                    names.add("..");
                    nodes.add(node.parent != null ? node.parent : node);
                }
                for (Node child : node.children.values()) {
                    if (showAll || !child.name.startsWith(".")) {
                        names.add(child.name);
                        nodes.add(child);
                    }
                }
            }

            if (longFormat) {
                if (node.isDirectory) print("total " + nodes.size() * 4);
                for (int j = 0; j < nodes.size(); j++) {
                    printMarkup(longEntry(nodes.get(j), names.get(j)));
                }
            } else {
                printColumns(names, nodes);
            }
        }
    }

    private void printColumns(List<String> names, List<Node> nodes) {
        StringBuilder line = new StringBuilder();
        int width = 0;
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            if (width > 0 && width + 2 + name.length() > columns) {
                printMarkup(line.toString());
                line.setLength(0);
                width = 0;
            }
            if (width > 0) {
                line.append("  ");
                width += 2;
            }
            line.append(colorName(nodes.get(i), name));
            width += name.length();
        }
        if (width > 0) printMarkup(line.toString());
    }

    private static String longEntry(Node node, String name) {
        String permissions;
        if (node.isDirectory) {
            permissions = node.rootOnly ? "drwx------" : "drwxr-xr-x";
        } else if (node.rootOnly) {
            permissions = "-rw-r-----";
        } else {
            permissions = node.executable ? "-rwxr-xr-x" : "-rw-r--r--";
        }
        int size = node.isDirectory ? 4096 : node.content.length();
        String date = new SimpleDateFormat("MMM d HH:mm", Locale.US).format(new Date(node.modified));
        return escape(String.format(Locale.US, "%s %d %s %s %5d %s ",
            permissions, node.isDirectory ? 2 : 1, node.owner, node.owner, size, date))
            + colorName(node, name);
    }

    private static String colorName(Node node, String name) {
        if (node.isDirectory) return color(COLOR_DIRECTORY, name);
        if (node.executable) return color(COLOR_EXECUTABLE, name);
        return escape(name);
    }

    private void cd(List<String> args) {
        if (args.size() > 1) {
            error("cd: too many arguments");
            return;
        }
        String target = args.isEmpty() ? "~" : args.get(0);
        Node node;
        if (target.equals("-")) {
            node = previousDirectory;
            print(node.path());
        } else {
            node = resolve(target);
        }
        if (node == null) {
            error("cd: no such file or directory: " + target);
        } else if (!node.isDirectory) {
            error("cd: not a directory: " + target);
        } else if (!canRead(node)) {
            error("cd: permission denied: " + target);
        } else {
            previousDirectory = cwd;
            cwd = node;
        }
    }

    private void cat(List<String> args) {
        if (args.isEmpty()) {
            error("cat: missing file operand");
            return;
        }
        for (String path : args) {
            Node node = resolve(path);
            if (node == null) {
                error("cat: " + path + ": No such file or directory");
            } else if (node.isDirectory) {
                error("cat: " + path + ": Is a directory");
            } else if (!canRead(node)) {
                error("cat: " + path + ": Permission denied");
            } else {
                markIfFlag(node);
                for (String line : linesOf(node.content)) print(line);
            }
        }
    }

    private void markIfFlag(Node node) {
        if (node == flagFile) {
            flagRead = true;
        }
    }

    private void touch(List<String> args) {
        if (args.isEmpty()) {
            error("touch: missing file operand");
            return;
        }
        for (String path : args) {
            Node node = resolve(path);
            if (node != null) {
                if (canWrite(node)) {
                    node.modified = System.currentTimeMillis();
                } else {
                    error("touch: cannot touch '" + path + "': Permission denied");
                }
                continue;
            }
            Node parent = parentOf(path);
            String name = baseName(path);
            if (parent == null || !parent.isDirectory || name.isEmpty()) {
                error("touch: cannot touch '" + path + "': No such file or directory");
            } else if (!canWrite(parent)) {
                error("touch: cannot touch '" + path + "': Permission denied");
            } else {
                create(parent, name, false);
            }
        }
    }

    private void mkdir(List<String> args) {
        List<String> paths = new ArrayList<>();
        boolean withParents = flags(args, paths).indexOf('p') >= 0;
        if (paths.isEmpty()) {
            error("mkdir: missing operand");
            return;
        }
        for (String path : paths) {
            if (withParents) {
                mkdirs(path);
                continue;
            }
            if (resolve(path) != null) {
                error("mkdir: cannot create directory '" + path + "': File exists");
                continue;
            }
            Node parent = parentOf(path);
            String name = baseName(path);
            if (parent == null || !parent.isDirectory || name.isEmpty()) {
                error("mkdir: cannot create directory '" + path + "': No such file or directory");
            } else if (!canWrite(parent)) {
                error("mkdir: cannot create directory '" + path + "': Permission denied");
            } else {
                create(parent, name, true);
            }
        }
    }

    private void mkdirs(String path) {
        Node node = startOf(path);
        for (String part : partsOf(path)) {
            if (node == null) break;
            if (part.isEmpty() || part.equals(".")) continue;
            if (part.equals("..")) {
                if (node.parent != null) node = node.parent;
                continue;
            }
            Node next = node.children.get(part);
            if (next == null) {
                if (!canWrite(node)) {
                    error("mkdir: cannot create directory '" + path + "': Permission denied");
                    return;
                }
                next = create(node, part, true);
            } else if (!next.isDirectory) {
                error("mkdir: cannot create directory '" + path + "': Not a directory");
                return;
            }
            node = next;
        }
        if (node == null)
            error("mkdir: cannot create directory '" + path + "': No such file or directory");
    }

    private void cp(List<String> args) {
        List<String> paths = new ArrayList<>();
        String flags = flags(args, paths);
        boolean recursive = flags.indexOf('r') >= 0 || flags.indexOf('R') >= 0;
        if (!hasSourceAndDestination("cp", paths)) return;

        String destination = paths.get(paths.size() - 1);
        for (String source : paths.subList(0, paths.size() - 1)) {
            Node node = resolve(source);
            if (node == null) {
                error("cp: cannot stat '" + source + "': No such file or directory");
                continue;
            }
            if (!canRead(node)) {
                error("cp: cannot open '" + source + "' for reading: Permission denied");
                continue;
            }
            if (node.isDirectory && !recursive) {
                error("cp: -r not specified; omitting directory '" + source + "'");
                continue;
            }
            Placement target = placementFor("cp", destination, node.name);
            if (target == null) continue;
            if (target.parent.isInside(node)) {
                error("cp: cannot copy a directory, '" + source + "', into itself, '" + destination + "'");
                continue;
            }
            Node existing = target.parent.children.get(target.name);
            if (existing != null && (existing.isDirectory || node.isDirectory)) {
                error("cp: cannot overwrite '" + destination + "'");
                continue;
            }
            attach(target.parent, copyOf(node, target.name));
        }
    }

    private void mv(List<String> args) {
        List<String> paths = new ArrayList<>();
        flags(args, paths);
        if (!hasSourceAndDestination("mv", paths)) return;

        String destination = paths.get(paths.size() - 1);
        for (String source : paths.subList(0, paths.size() - 1)) {
            Node node = resolve(source);
            if (node == null) {
                error("mv: cannot stat '" + source + "': No such file or directory");
                continue;
            }
            if (node == rootDirectory || !canWrite(node.parent)) {
                error("mv: cannot move '" + source + "' to '" + destination + "': Permission denied");
                continue;
            }
            Placement target = placementFor("mv", destination, node.name);
            if (target == null) continue;
            if (target.parent.isInside(node)) {
                error("mv: cannot move '" + source + "' to a subdirectory of itself, '" + destination + "'");
                continue;
            }
            Node existing = target.parent.children.get(target.name);
            if (existing == node) continue;
            if (existing != null && (existing.isDirectory || node.isDirectory)) {
                error("mv: cannot overwrite '" + destination + "'");
                continue;
            }
            node.parent.children.remove(node.name);
            node.name = target.name;
            attach(target.parent, node);
        }
    }

    private boolean hasSourceAndDestination(String command, List<String> paths) {
        if (paths.isEmpty()) {
            error(command + ": missing file operand");
            return false;
        }
        if (paths.size() == 1) {
            error(command + ": missing destination file operand after '" + paths.get(0) + "'");
            return false;
        }
        return true;
    }

    private Placement placementFor(String command, String destination, String sourceName) {
        Node target = resolve(destination);
        Node parent;
        String name;
        if (target != null && target.isDirectory) {
            parent = target;
            name = sourceName;
        } else {
            parent = parentOf(destination);
            name = baseName(destination);
        }
        if (parent == null || !parent.isDirectory || name.isEmpty()) {
            error(command + ": cannot create '" + destination + "': No such file or directory");
            return null;
        }
        if (!canWrite(parent)) {
            error(command + ": cannot create '" + destination + "': Permission denied");
            return null;
        }
        return new Placement(parent, name);
    }

    private void rm(List<String> args) {
        List<String> paths = new ArrayList<>();
        String flags = flags(args, paths);
        boolean recursive = flags.indexOf('r') >= 0 || flags.indexOf('R') >= 0;
        boolean force = flags.indexOf('f') >= 0;
        if (paths.isEmpty()) {
            if (!force) error("rm: missing operand");
            return;
        }
        for (String path : paths) {
            String name = stripTrailingSlashes(path);
            name = name.substring(name.lastIndexOf('/') + 1);
            if (name.equals(".") || name.equals("..")) {
                error("rm: refusing to remove '.' or '..' directory: skipping '" + path + "'");
                continue;
            }
            Node node = resolve(path);
            if (node == null) {
                if (!force) error("rm: cannot remove '" + path + "': No such file or directory");
            } else if (node.isDirectory && !recursive) {
                error("rm: cannot remove '" + path + "': Is a directory");
            } else if (node == rootDirectory) {
                error("rm: it is dangerous to operate recursively on '/'");
                error("rm: use --no-preserve-root to override this failsafe");
            } else if (!canWrite(node.parent) || (node.isDirectory && !canRead(node))) {
                error("rm: cannot remove '" + path + "': Permission denied");
            } else {
                remove(node);
            }
        }
    }

    private void rmdir(List<String> args) {
        if (args.isEmpty()) {
            error("rmdir: missing operand");
            return;
        }
        for (String path : args) {
            Node node = resolve(path);
            if (node == null) {
                error("rmdir: failed to remove '" + path + "': No such file or directory");
            } else if (!node.isDirectory) {
                error("rmdir: failed to remove '" + path + "': Not a directory");
            } else if (!node.children.isEmpty() || node == rootDirectory) {
                error("rmdir: failed to remove '" + path + "': Directory not empty");
            } else if (!canWrite(node.parent)) {
                error("rmdir: failed to remove '" + path + "': Permission denied");
            } else {
                remove(node);
            }
        }
    }

    private void grep(List<String> args) {
        List<String> operands = new ArrayList<>();
        boolean ignoreCase = flags(args, operands).indexOf('i') >= 0;
        if (operands.size() < 2) {
            error("Usage: grep [OPTION]... PATTERNS [FILE]...");
            return;
        }
        String pattern = operands.get(0);
        List<String> files = operands.subList(1, operands.size());
        for (String path : files) {
            Node node = resolve(path);
            if (node == null) {
                error("grep: " + path + ": No such file or directory");
            } else if (node.isDirectory) {
                error("grep: " + path + ": Is a directory");
            } else if (!canRead(node)) {
                error("grep: " + path + ": Permission denied");
            } else {
                markIfFlag(node);
                String prefix = files.size() > 1
                    ? color(COLOR_GREP_FILE, path) + color(COLOR_GREP_SEPARATOR, ":")
                    : "";
                for (String line : linesOf(node.content)) {
                    String highlighted = highlight(line, pattern, ignoreCase);
                    if (highlighted != null) printMarkup(prefix + highlighted);
                }
            }
        }
    }

    private static String highlight(String line, String pattern, boolean ignoreCase) {
        if (pattern.isEmpty()) return escape(line);
        String haystack = ignoreCase ? line.toLowerCase(Locale.ROOT) : line;
        String needle = ignoreCase ? pattern.toLowerCase(Locale.ROOT) : pattern;
        int index = haystack.indexOf(needle);
        if (index < 0) return null;

        StringBuilder result = new StringBuilder();
        int from = 0;
        while (index >= 0) {
            result.append(escape(line.substring(from, index)))
                .append(color(COLOR_MATCH, line.substring(index, index + needle.length())));
            from = index + needle.length();
            index = haystack.indexOf(needle, from);
        }
        return result.append(escape(line.substring(from))).toString();
    }

    private void uname(List<String> args) {
        List<String> operands = new ArrayList<>();
        String flags = flags(args, operands);
        if (!operands.isEmpty()) {
            error("uname: extra operand '" + operands.get(0) + "'");
            return;
        }
        if (flags.isEmpty()) flags = "s";
        if (flags.indexOf('a') >= 0) flags = "snrvmo";

        List<String> parts = new ArrayList<>();
        if (flags.indexOf('s') >= 0) parts.add("Linux");
        if (flags.indexOf('n') >= 0) parts.add(HOST);
        if (flags.indexOf('r') >= 0) parts.add(KERNEL_RELEASE);
        if (flags.indexOf('v') >= 0) parts.add(KERNEL_VERSION);
        if (flags.indexOf('m') >= 0) parts.add("x86_64");
        if (flags.indexOf('o') >= 0) parts.add("GNU/Linux");
        print(String.join(" ", parts));
    }

    private void ifconfig() {
        print("eth0: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1500");
        print("        inet 10.0.2.15  netmask 255.255.255.0  broadcast 10.0.2.255");
        print("        inet6 fe80::a00:27ff:fe4e:66a1  prefixlen 64  scopeid 0x20<link>");
        print("        ether 08:00:27:4e:66:a1  txqueuelen 1000  (Ethernet)");
        print("        RX packets 1024  bytes 1048576 (1.0 MiB)");
        print("        TX packets 512  bytes 65536 (64.0 KiB)");
        print("");
        print("lo: flags=73<UP,LOOPBACK,RUNNING>  mtu 65536");
        print("        inet 127.0.0.1  netmask 255.0.0.0");
        print("        inet6 ::1  prefixlen 128  scopeid 0x10<host>");
        print("        loop  txqueuelen 1000  (Local Loopback)");
    }

    private void sudo(List<String> args) {
        if (args.isEmpty()) {
            error("usage: sudo -h | -K | -k | -V");
            error("usage: sudo [-u user] command [arg ...]");
            return;
        }
        String command = args.get(0);
        if (!COMMAND_SET.contains(command) || BUILTINS.contains(command)) {
            error("sudo: " + command + ": command not found");
            return;
        }
        boolean wasRoot = asRoot;
        asRoot = true;
        try {
            runCommand(command, args.subList(1, args.size()));
        } finally {
            asRoot = wasRoot;
        }
    }

    private void writeFile(String path, String text, boolean append) {
        Node node = resolve(path);
        if (node != null && node.isDirectory) {
            error("zsh: is a directory: " + path);
            return;
        }
        if (node == null) {
            Node parent = parentOf(path);
            String name = baseName(path);
            if (parent == null || !parent.isDirectory || name.isEmpty()) {
                error("zsh: no such file or directory: " + path);
                return;
            }
            if (!canWrite(parent)) {
                error("zsh: permission denied: " + path);
                return;
            }
            node = create(parent, name, false);
        } else if (!canWrite(node)) {
            error("zsh: permission denied: " + path);
            return;
        }
        node.content = append ? node.content + text : text;
        node.modified = System.currentTimeMillis();
    }

    private Node resolve(String path) {
        if (path.isEmpty()) return null;
        Node node = startOf(path);
        for (String part : partsOf(path)) {
            if (node == null) return null;
            if (part.isEmpty() || part.equals(".")) continue;
            if (part.equals("..")) {
                if (node.parent != null) node = node.parent;
                continue;
            }
            if (!node.isDirectory) return null;
            node = node.children.get(part);
        }
        return node;
    }

    private Node startOf(String path) {
        if (path.startsWith("/")) return rootDirectory;
        if (path.equals("~") || path.startsWith("~/")) return isAttached(home) ? home : null;
        return cwd;
    }

    private static String[] partsOf(String path) {
        boolean fromHome = path.equals("~") || path.startsWith("~/");
        return (fromHome ? path.substring(1) : path).split("/");
    }

    private Node parentOf(String path) {
        String trimmed = stripTrailingSlashes(path);
        int slash = trimmed.lastIndexOf('/');
        if (slash < 0) return trimmed.equals("~") ? null : cwd;
        if (slash == 0) return rootDirectory;
        return resolve(trimmed.substring(0, slash));
    }

    private static String baseName(String path) {
        String trimmed = stripTrailingSlashes(path);
        String name = trimmed.substring(trimmed.lastIndexOf('/') + 1);
        return name.equals(".") || name.equals("..") || name.equals("~") ? "" : name;
    }

    private static String stripTrailingSlashes(String path) {
        String result = path;
        while (result.length() > 1 && result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    private boolean isAttached(Node node) {
        return node.isInside(rootDirectory);
    }

    private boolean canRead(Node node) {
        if (asRoot) return true;
        for (Node current = node; current != null; current = current.parent) {
            if (current.rootOnly) return false;
        }
        return true;
    }

    private boolean canWrite(Node node) {
        return asRoot || (canRead(node) && (node.owner.equals(USER) || node == tmp));
    }

    private Node create(Node parent, String name, boolean isDirectory) {
        Node node = new Node(name, isDirectory, asRoot ? "root" : USER);
        attach(parent, node);
        return node;
    }

    private static void attach(Node parent, Node node) {
        node.parent = parent;
        parent.children.put(node.name, node);
        parent.modified = System.currentTimeMillis();
    }

    private Node copyOf(Node source, String name) {
        Node copy = new Node(name, source.isDirectory, asRoot ? "root" : USER);
        copy.content = source.content;
        copy.executable = source.executable;
        for (Node child : source.children.values()) {
            attach(copy, copyOf(child, child.name));
        }
        return copy;
    }

    private void remove(Node node) {
        Node parent = node.parent;
        parent.children.remove(node.name);
        parent.modified = System.currentTimeMillis();
        node.parent = null;
        if (!isAttached(cwd)) cwd = parent;
        if (!isAttached(previousDirectory)) previousDirectory = parent;
    }

    private String displayPath() {
        if (isAttached(home) && cwd.isInside(home)) {
            return cwd == home ? "~" : "~" + cwd.path().substring(home.path().length());
        }
        return cwd.path();
    }

    private static String flags(List<String> args, List<String> operands) {
        StringBuilder flags = new StringBuilder();
        for (String arg : args) {
            if (arg.length() > 1 && arg.charAt(0) == '-') {
                flags.append(arg, 1, arg.length());
            } else {
                operands.add(arg);
            }
        }
        return flags.toString();
    }

    private static String[] linesOf(String content) {
        if (content.isEmpty()) return new String[0];
        String body = content.endsWith("\n") ? content.substring(0, content.length() - 1) : content;
        return body.split("\n", -1);
    }

    private static String commonPrefix(List<String> values) {
        String prefix = values.get(0);
        for (String value : values) {
            int length = 0;
            while (length < prefix.length() && length < value.length()
                && prefix.charAt(length) == value.charAt(length)) {
                length++;
            }
            prefix = prefix.substring(0, length);
        }
        return prefix;
    }

    private void print(String text) {
        output.add(new Line(escape(text), false));
    }

    private void printMarkup(String markup) {
        output.add(new Line(markup, false));
    }

    private void error(String text) {
        output.add(new Line(escape(text), true));
    }

    private void buildFileSystem() {
        rootDirectory = new Node("", true, "root");
        for (String name : new String[]{"boot", "dev", "lib", "media", "mnt", "opt", "proc", "run", "srv", "sys"}) {
            directory(rootDirectory, name, "root");
        }

        Node bin = directory(rootDirectory, "bin", "root");
        Node usrBin = directory(directory(rootDirectory, "usr", "root"), "bin", "root");
        for (String command : COMMANDS) {
            if (BUILTINS.contains(command)) continue;
            file(bin, command, "root", "").executable = true;
            file(usrBin, command, "root", "").executable = true;
        }

        Node etc = directory(rootDirectory, "etc", "root");
        file(etc, "hostname", "root", HOST + "\n");
        file(etc, "hosts", "root", "127.0.0.1       localhost\n127.0.1.1       " + HOST + "\n");
        file(etc, "os-release", "root",
            "PRETTY_NAME=\"Kali GNU/Linux Rolling\"\n"
                + "NAME=\"Kali GNU/Linux\"\n"
                + "VERSION_ID=\"2025.2\"\n"
                + "VERSION=\"2025.2\"\n"
                + "VERSION_CODENAME=kali-rolling\n"
                + "ID=kali\n"
                + "ID_LIKE=debian\n"
                + "HOME_URL=\"https://www.kali.org/\"\n");
        file(etc, "passwd", "root",
            "root:x:0:0:root:/root:/usr/bin/zsh\n"
                + "daemon:x:1:1:daemon:/usr/sbin:/usr/sbin/nologin\n"
                + "www-data:x:33:33:www-data:/var/www:/usr/sbin/nologin\n"
                + "kali:x:1000:1000:Kali,,,:/home/kali:/usr/bin/zsh\n");
        file(etc, "shadow", "root",
            "root:!:20211:0:99999:7:::\n"
                + "kali:$y$j9T$Wq3.8rKXN0Jp1nB2$T4v7mDz0gQh9sLc2eUe5pYxR1aZ8oFwK3nM6tHjV1bC:20211:0:99999:7:::\n")
            .rootOnly = true;

        Node rootHome = directory(rootDirectory, "root", "root");
        rootHome.rootOnly = true;
        file(rootHome, ".zshrc", "root", "# ~/.zshrc file for zsh interactive shells.\n");

        flagFile = file(rootDirectory, "flag", "root", FLAG + "\n");
        flagFile.rootOnly = true;

        tmp = directory(rootDirectory, "tmp", "root");
        Node var = directory(rootDirectory, "var", "root");
        directory(var, "log", "root");
        directory(var, "www", "root");

        home = directory(directory(rootDirectory, "home", "root"), USER, USER);
        for (String name : new String[]{"Desktop", "Documents", "Downloads", "Music", "Pictures", "Public", "Templates", "Videos"}) {
            directory(home, name, USER);
        }
        file(home, ".zshrc", USER,
            "# ~/.zshrc file for zsh interactive shells.\n"
                + "HISTFILE=~/.zsh_history\n"
                + "HISTSIZE=1000\n"
                + "SAVEHIST=2000\n");
        file(home.children.get("Documents"), "notes.txt", USER,
            "Linux cheat sheet\n"
                + "-----------------\n"
                + "ls        list files\n"
                + "cd DIR    change directory\n"
                + "cat FILE  print a file\n"
                + "grep      search text in files\n"
                + "sudo      run a command as root\n"
                + "\n"
                + "Task: read the file /flag\n"
                + "Only root may read it.\n");
    }

    private static Node directory(Node parent, String name, String owner) {
        Node node = new Node(name, true, owner);
        attach(parent, node);
        return node;
    }

    private static Node file(Node parent, String name, String owner, String content) {
        Node node = new Node(name, false, owner);
        node.content = content;
        attach(parent, node);
        return node;
    }
}
