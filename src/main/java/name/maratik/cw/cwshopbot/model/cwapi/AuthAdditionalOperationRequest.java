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
public class AuthAdditionalOperationRequest implements ApiRequestWithPayload<AuthAdditionalOperationRequest.Payload> {
    private final Payload payload;
    private final String token;

    private AuthAdditionalOperationRequest(Payload payload, String token) {
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
        return "AuthAdditionalOperationRequest{" +
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

        public AuthAdditionalOperationRequest build() {
            return new AuthAdditionalOperationRequest(payload, token);
        }
    }

    public static class Payload implements RequestPayload {
        private final long userId;
        private final Operation operation;

        private Payload(long userId, Operation operation) {
            this.userId = userId;
            this.operation = Objects.requireNonNull(operation, "operation");
        }

        public long getUserId() {
            return userId;
        }

        public Operation getOperation() {
            return operation;
        }

        @Override
        public String toString() {
            return "Payload{" +
                "userId=" + userId +
                ", operation=" + operation +
                '}';
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private long userId;
            private Operation operation;

            public Builder setUserId(long userId) {
                this.userId = userId;
                return this;
            }

            public Builder setOperation(Operation operation) {
                this.operation = operation;
                return this;
            }

            public Payload build() {
                return new Payload(userId, operation);
            }
        }
    }
}
