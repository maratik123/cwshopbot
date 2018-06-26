//    cwshopbot
//    Copyright (C) 2018  Marat Bukharov.
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Affero General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Affero General Public License for more details.
//
//    You should have received a copy of the GNU Affero General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
package name.maratik.cw.cwshopbot.model.cwapi;

import java.util.Objects;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class RequestStockRequest implements ApiRequest {
    private final String token;

    private RequestStockRequest(String token) {
        this.token = Objects.requireNonNull(token, "token");
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "RequestStockRequest{" +
            "token='" + token + '\'' +
            '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String token;

        public Builder setToken(String token) {
            this.token = token;
            return this;
        }

        public RequestStockRequest build() {
            return new RequestStockRequest(token);
        }
    }
}
