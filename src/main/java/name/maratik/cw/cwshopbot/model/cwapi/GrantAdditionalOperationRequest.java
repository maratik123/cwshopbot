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
public class GrantAdditionalOperationRequest implements ApiRequestWithPayload<GrantAdditionalOperationRequest.Payload> {
    private final Payload payload;
    private final String token;

    private GrantAdditionalOperationRequest(Payload payload, String token) {
        this.payload = Objects.requireNonNull(payload, "payload");
        this.token = Objects.requireNonNull(token, "token");
    }

    @Override
    public Payload getPayload() {
        return payload;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "GrantAdditionalOperationRequest{" +
            "payload=" + payload +
            ", token='" + token + '\'' +
            '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Payload payload;
        private String token;

        public Builder setPayload(Payload payload) {
            this.payload = payload;
            return this;
        }

        public Builder setToken(String token) {
            this.token = token;
            return this;
        }

        public GrantAdditionalOperationRequest build() {
            return new GrantAdditionalOperationRequest(payload, token);
        }
    }

    public static class Payload implements RequestPayload {
        private final String requestId;
        private final String authCode;

        private Payload(String requestId, String authCode) {
            this.requestId = Objects.requireNonNull(requestId, "requestId");
            this.authCode = Objects.requireNonNull(authCode, "authCode");
        }

        public String getRequestId() {
            return requestId;
        }

        public String getAuthCode() {
            return authCode;
        }

        @Override
        public String toString() {
            return "Payload{" +
                "requestId='" + requestId + '\'' +
                ", authCode='" + authCode + '\'' +
                '}';
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String requestId;
            private String authCode;

            public Builder setRequestId(String requestId) {
                this.requestId = requestId;
                return this;
            }

            public Builder setAuthCode(String authCode) {
                this.authCode = authCode;
                return this;
            }

            public Payload build() {
                return new Payload(requestId, authCode);
            }
        }
    }
}
