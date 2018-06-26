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

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public abstract class ApiResponseBase<T extends ResponsePayload> implements ApiResponse<T> {
    private final String uuid;
    private final ChatWarsApiResult result;
    private final T payload;

    protected ApiResponseBase(String uuid, ChatWarsApiResult result, T payload) {
        this.uuid = uuid;
        this.result = result;
        this.payload = payload;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public ChatWarsApiResult getResult() {
        return result;
    }

    @Override
    public T getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "ApiResponseBase{" +
            "uuid='" + uuid + '\'' +
            ", result=" + result +
            ", payload=" + payload +
            '}';
    }
}
