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
package name.maratik.cw.eu.spring.model;

import name.maratik.cw.eu.spring.annotation.TelegramCommand;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class TelegramHandler {
    private final Object bean;
    private final Method method;
    private final TelegramCommand telegramCommand;

    public TelegramHandler(Object bean, Method method, TelegramCommand telegramCommand) {
        this.bean = bean;
        this.method = method;
        this.telegramCommand = telegramCommand;
    }

    public Object getBean() {
        return bean;
    }

    public Method getMethod() {
        return method;
    }

    public Optional<TelegramCommand> getTelegramCommand() {
        return Optional.ofNullable(telegramCommand);
    }

    @Override
    public String toString() {
        return "TelegramHandler{" +
            "bean=" + bean +
            ", method=" + method +
            ", telegramCommand=" + telegramCommand +
            '}';
    }
}
