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
public class GrantTokenRequest implements ApiRequestWithPayload<GrantTokenRequest.Payload> {
    private final Payload payload;

    private GrantTokenRequest(Payload payload) {
        this.payload = Objects.requireNonNull(payload, "payload");
    }

    @Override
    public Payload getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "GrantTokenRequest{" +
            "payload=" + payload +
            '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Payload payload;

        public Builder setPayload(Payload payload) {
            this.payload = payload;
            return this;
        }

        public GrantTokenRequest build() {
            return new GrantTokenRequest(payload);
        }
    }

    public static class Payload implements RequestPayload {
        private final long userId;
        private final String authCode;

        private Payload(long userId, String authCode) {
            this.userId = userId;
            this.authCode = Objects.requireNonNull(authCode, "authCode");
        }

        public long getUserId() {
            return userId;
        }

        public String getAuthCode() {
            return authCode;
        }

        @Override
        public String toString() {
            return "Payload{" +
                "userId=" + userId +
                ", authCode='" + authCode + '\'' +
                '}';
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private long userId;
            private String authCode;

            public Builder setUserId(long userId) {
                this.userId = userId;
                return this;
            }

            public Builder setAuthCode(String authCode) {
                this.authCode = authCode;
                return this;
            }

            public Payload build() {
                return new Payload(userId, authCode);
            }
        }
    }
}
