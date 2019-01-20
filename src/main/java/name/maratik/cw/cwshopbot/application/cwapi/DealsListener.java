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
package name.maratik.cw.cwshopbot.application.cwapi;

import name.maratik.cw.cwshopbot.application.storage.DealStorage;
import name.maratik.cw.cwshopbot.model.cwapi.Deal;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Service
@Log4j2
public class DealsListener {
    private final DealStorage dealStorage;

    public DealsListener(DealStorage dealStorage) {
        this.dealStorage = dealStorage;
    }

    @RabbitListener(queues = "${spring.rabbitmq.username}_deals")
    public void processDealsAnnounce(Deal data) {
        log.debug("Received message: {}", data);
        dealStorage.saveDeal(data);
    }
}
