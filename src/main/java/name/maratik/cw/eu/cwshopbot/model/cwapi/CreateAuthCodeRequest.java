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
package name.maratik.cw.eu.cwshopbot.model.cwapi;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class CreateAuthCodeRequest implements ApiRequest<CreateAuthCodeRequest.Payload> {
    private final Payload payload;

    private CreateAuthCodeRequest(Payload payload) {
        this.payload = payload;
    }

    @Override
    public Payload getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "CreateAuthCodeRequest{" +
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

        public CreateAuthCodeRequest build() {
            return new CreateAuthCodeRequest(payload);
        }
    }

    public static class Payload implements RequestPayload {
        private final long userId;

        private Payload(long userId) {
            this.userId = userId;
        }

        public long getUserId() {
            return userId;
        }

        @Override
        public String toString() {
            return "Payload{" +
                "userId=" + userId +
                '}';
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private long userId;

            public Builder setUserId(long userId) {
                this.userId = userId;
                return this;
            }

            public Payload build() {
                return new Payload(userId);
            }
        }
    }
}
