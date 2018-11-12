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
package name.maratik.cw.cwshopbot.application.botcontroller;

import name.maratik.cw.cwshopbot.application.service.StatsService;
import name.maratik.spring.telegram.annotation.TelegramBot;
import name.maratik.spring.telegram.annotation.TelegramForward;
import name.maratik.spring.telegram.annotation.TelegramMessage;

import org.telegram.telegrambots.meta.api.objects.User;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@TelegramBot("${name.maratik.cw.cwshopbot.ban}")
public class BannedController {
    private final StatsService statsService;

    public BannedController(StatsService statsService) {
        this.statsService = statsService;
    }

    @TelegramMessage
    public void message(User user) {
        statsService.updateStats("banned.message", user);
    }

    @TelegramForward
    public void forward(User user) {
        statsService.updateStats("banned.forward", user);
    }
}
