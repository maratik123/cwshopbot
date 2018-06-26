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
package name.maratik.cw.cwshopbot.mock;

import org.telegram.telegrambots.api.objects.MessageEntity;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class MockMessageEntity extends MessageEntity {
    private static final long serialVersionUID = -7927225285130441258L;
    private final String type;
    private final int offset;
    private final int length;

    public MockMessageEntity(String type, int offset, int length) {
        this.type = type;
        this.offset = offset;
        this.length = length;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Integer getOffset() {
        return offset;
    }

    @Override
    public Integer getLength() {
        return length;
    }
}
