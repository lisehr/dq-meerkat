/*
 * *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * /
 */

package at.scch.dqm.dqmeerkat;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import science.aist.seshat.Logger;

@SpringBootApplication
public class DqmeerkatApplication implements CommandLineRunner {

    private static final Logger LOGGER = Logger.getInstance();

    public static void main(String[] args) {
        SpringApplication.run(DqmeerkatApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("DqmeerkatApplication started!");
        LOGGER.info("Hello there!");
    }
}
