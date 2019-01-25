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
package name.maratik.cw.cwshopbot.application.repository.account;

import name.maratik.cw.cwshopbot.entity.AccountEntity;
import name.maratik.cw.cwshopbot.util.Utils;

import com.google.common.collect.ImmutableMap;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import static name.maratik.cw.cwshopbot.util.Utils.text;
import static name.maratik.cw.cwshopbot.util.Utils.timestamp;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Repository
public class CustomizedAccountRepositoryImpl implements CustomizedAccountRepository {
    private static final String SAVE_ACCOUNT = "" +
        "WITH INS AS(" +
        "  INSERT INTO ACCOUNT(EXTERNAL_ID, NAME, CASTLE, CREATION_TIME)" +
        "  VALUES(:externalId::text, :name::text, :castle::text, :creationTime::timestamp)" +
        "  on conflict(EXTERNAL_ID, NAME, CASTLE) do nothing" +
        "  returning ID, CREATION_TIME)," +
        "SEL as(" +
        "  TABLE INS" +
        "  union all" +
        "  select ID, CREATION_TIME" +
        "    from ACCOUNT" +
        "   where EXTERNAL_ID = :externalId::text" +
        "     and NAME = :name::text" +
        "     and CASTLE = :castle::text)," +
        "UPS as (" +
        "  insert into ACCOUNT as A(EXTERNAL_ID, NAME, CASTLE, CREATION_TIME)" +
        "  select :externalId::text, :name::text, :castle::text, :creationTime::timestamp" +
        "   where not exists(table SEL)" +
        "  on conflict(EXTERNAL_ID, NAME, CASTLE) do update set CREATION_TIME = A.CREATION_TIME" +
        "  returning ID, CREATION_TIME)" +
        "table SEL " +
        "union all " +
        "table UPS";
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public CustomizedAccountRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public AccountEntity save(AccountEntity accountEntity) {
        return accountEntity.withIdAndCreationTime(Utils.requireNonNull(namedParameterJdbcTemplate.queryForObject(
            SAVE_ACCOUNT,
            ImmutableMap.of(
                "externalId", text(accountEntity.getExternalId()),
                "name", text(accountEntity.getName()),
                "castle", text(accountEntity.getCastle()),
                "creationTime", timestamp(accountEntity.getCreationTime())
            ),
            AccountEntity.IdWithCreationTime::rowMapper
        )));
    }

}
