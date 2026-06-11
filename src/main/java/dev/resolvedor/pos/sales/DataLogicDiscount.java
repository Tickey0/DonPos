package dev.resolvedor.pos.sales;

import com.unicenta.basic.BasicException;
import com.unicenta.data.loader.Datas;
import com.unicenta.data.loader.SentenceList;
import com.unicenta.data.loader.SerializerReadClass;
import com.unicenta.data.loader.SerializerWriteBasic;
import com.unicenta.data.loader.SerializerWriteInteger;
import com.unicenta.data.loader.Session;
import com.unicenta.data.loader.StaticSentence;
import com.unicenta.pos.forms.BeanFactoryDataSingle;

/**
 *
 * @author Jorge Luis
 * @web https://resolvedor.dev
 * @mail jorgeluis@resolvedor.dev
 */
public class DataLogicDiscount extends BeanFactoryDataSingle {

    private Session s;

    @Override
    public void init(Session s) {
        this.s = s;
    }

    public final SentenceList getCategoryList() throws BasicException {
        return new StaticSentence(s,
                "select id, name from categories "
                + "order by name",
                null,
                new SerializerReadClass(CategoryInfo.class));
    }

    public final SentenceList getVolumeDiscountList(String categoryId) throws BasicException {
        return new StaticSentence(s,
                "SELECT d.id, p.code, p.name, p.pricesell, d.minimum_quantity, d.value, d.status "
                + "from products p inner join volume_discount d on p.id = d.product "
                + "where (p.category = '" + categoryId + "' or '' = '" + categoryId + "')"
                + "order by p.name asc",
                null,
                new SerializerReadClass(VolumeDiscountInfo.class));
    }

    public final int add(VolumeDiscountInfo discount) throws BasicException {
        var result = new StaticSentence(
                s,
                "insert into volume_discount (product, minimum_quantity, value) values (?, ?, ?)",
                new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.INT, Datas.DOUBLE})
        ).exec(new Object[]{
            discount.getProduct().getID(),
            discount.getMinimumQuantity(),
            discount.getValue()
        });

        return result;
    }

    public final void delete(Integer id) throws BasicException {
        new StaticSentence(
                s,
                "delete from volume_discount where id = ?",
                SerializerWriteInteger.INSTANCE
        ).exec(id);
    }

    public void updateDiscount(Integer id, double value) throws BasicException {
        //TODO discounts
        /*
        new StaticSentence(s,
                "UPDATE products SET discount = ? WHERE id = ?",
                new SerializerWriteBasic(new Datas[]{Datas.DOUBLE, Datas.STRING}))
                .exec(new Object[]{value, productId});
         */
    }
}
