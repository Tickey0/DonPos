//    Mestizo Pos - Touch Friendly Point Of Sale
//    https://resolvedor.dev
//
//    Mestizo Pos is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Mestizo Pos is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Mestizo Pos.  If not, see <http://www.gnu.org/licenses/>.
package dev.joguenco.pos.taxpayer;

import com.unicenta.basic.BasicException;
import com.unicenta.data.loader.DataRead;
import com.unicenta.data.loader.SerializableRead;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jorgeluis
 */
public class TaxpayerInfo implements SerializableRead {

    @Getter
    @Setter
    private String identification;
    @Getter
    @Setter
    private String legalName;
    @Getter
    @Setter
    private String text1;
    @Getter
    @Setter
    private String text2;
    @Getter
    @Setter
    private String text3;
    @Getter
    @Setter
    private String text4;

    public TaxpayerInfo() {
        identification = "";
        legalName = "";
    }    
    
    @Override
    public void readValues(DataRead dr) throws BasicException {
        this.identification = dr.getString(1);
        this.legalName = dr.getString(2);
        this.text1 = dr.getString(3);
        this.text2 = dr.getString(4);
        this.text3 = dr.getString(5);
        this.text4 = dr.getString(6);
    }
}
