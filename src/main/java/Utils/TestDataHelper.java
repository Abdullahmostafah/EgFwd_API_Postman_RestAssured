package Utils;

public class TestDataHelper {
    public static String getOrganizationName() {
        return ConfigReaderWriter.getPropKey("test.organization.name");
    }

    public static String getBoardName() {
        return ConfigReaderWriter.getPropKey("test.board.name");
    }

    public static String getListName() {
        return ConfigReaderWriter.getPropKey("test.list.name");
    }

    public static String getExpectedUsername() {
        return ConfigReaderWriter.getPropKey("trello.username");
    }
}