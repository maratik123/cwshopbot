//    cwshopbot
//    Copyright (C) 2018  Marat Bukharov.
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
package name.maratik.cw.eu.spring.config;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class TelegramBotBuilder {

    public static final int DEFAULT_MAX_THREADS = 30;

    private String username;
    private String token;
    private String path;
    private int maxThreads = DEFAULT_MAX_THREADS;

    public TelegramBotBuilder() {
    }

    public TelegramBotBuilder(String username, String token) {
        this.username = username;
        this.token = token;
    }

    public TelegramBotBuilder username(String username) {
        this.username = username;
        return this;
    }

    public TelegramBotBuilder token(String token) {
        this.token = token;
        return this;
    }

    public TelegramBotBuilder path(String path) {
        this.path = path;
        return this;
    }

    public TelegramBotBuilder maxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    @Override
    public String toString() {
        return "TelegramBotBuilder{" +
            "username='" + username + '\'' +
            ", token='" + token + '\'' +
            ", path='" + path + '\'' +
            ", maxThreads=" + maxThreads +
            '}';
    }
}
