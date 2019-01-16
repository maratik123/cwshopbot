//    cwshopbot
//    Copyright (C) 2019  Marat Bukharov.
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

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@ToString(callSuper = true)
public class GetInfoResponse extends ApiResponseBase<GetInfoResponse.Payload> {

    public GetInfoResponse(
        @JsonProperty("uuid") String uuid,
        @JsonProperty("result") ChatWarsApiResult result,
        @JsonProperty("payload") Payload payload
    ) {
        super(uuid, result, payload);
    }

    @ToString
    public static class Payload implements ResponsePayload {
        @JsonIgnore
        @Getter(onMethod_ = {@JsonIgnore})
        private final Map<String, String> props = new HashMap<>();

        @JsonAnySetter
        public void putProp(String key, String value) {
            props.put(key, value);
        }
    }
}
