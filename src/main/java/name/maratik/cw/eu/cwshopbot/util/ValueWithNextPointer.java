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
package name.maratik.cw.eu.cwshopbot.util;

import org.telegram.telegrambots.api.objects.MessageEntity;

import java.util.Optional;
import java.util.function.Function;

import static name.maratik.cw.eu.cwshopbot.util.Utils.endOfEntity;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class ValueWithNextPointer<T> {
    private final int nextPointer;
    private final T value;

    public ValueWithNextPointer(int nextPointer, T value) {
        this.nextPointer = nextPointer;
        this.value = value;
    }

    public static <T> Optional<ValueWithNextPointer<T>> of(MessageEntity messageEntity,
                                                           Function<String, Optional<T>> valueExtractor) {
        return Optional.of(messageEntity.getText())
            .flatMap(valueExtractor)
            .map(value -> new ValueWithNextPointer<>(endOfEntity(messageEntity), value));
    }

    public int getNextPointer() {
        return nextPointer;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ValueWithNextPointer{" +
            "nextPointer=" + nextPointer +
            ", value=" + value +
            '}';
    }
}
