package dev.resolvedor.pos.sales;

import com.unicenta.basic.BasicException;
import com.unicenta.data.loader.Datas;
import com.unicenta.data.loader.SentenceList;
import com.unicenta.data.loader.SerializerReadClass;
import com.unicenta.data.loader.SerializerWriteBasic;
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

    public final SentenceList getProductList(String categoryId) throws BasicException {
        return new StaticSentence(s,
                "select id, name from products "
                + "where category = '" + categoryId + "' "
                + "order by name",
                null,
                new SerializerReadClass(ProductInfo.class));
    }

    public void updateProductCommission(String productId, double commission) throws BasicException {
        //TODO discounts
        /*
        new StaticSentence(s,
                "UPDATE products SET discount = ? WHERE id = ?",
                new SerializerWriteBasic(new Datas[]{Datas.DOUBLE, Datas.STRING}))
                .exec(new Object[]{commission, productId});
         */
    }
}
