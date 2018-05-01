package name.maratik.cw.eu.cwshopbot.model;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class ShopInfo {
    private final String shopName;
    private final String charName;
    private final String shopCommand;

    public ShopInfo(String shopName, String charName, String shopCommand) {
        this.shopName = shopName;
        this.charName = charName;
        this.shopCommand = shopCommand;
    }

    public String getShopName() {
        return shopName;
    }

    public String getCharName() {
        return charName;
    }

    public String getShopCommand() {
        return shopCommand;
    }

    @Override
    public String toString() {
        return "ShopInfo{" +
            "shopName='" + shopName + '\'' +
            ", charName='" + charName + '\'' +
            ", shopCommand='" + shopCommand + '\'' +
            '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String charName;
        private String shopName;
        private String shopCommand;

        public Builder setCharName(String charName) {
            this.charName = charName;
            return this;
        }

        public Builder setShopName(String shopName) {
            this.shopName = shopName;
            return this;
        }

        public Builder setShopCommand(String shopCommand) {
            this.shopCommand = shopCommand;
            return this;
        }

        public ShopInfo build() {
            return new ShopInfo(shopName, charName, shopCommand);
        }
    }
}

