package com.fyxridd.lib.show.chat.condition;

import org.da.expressionj.expr.parser.EquationParser;
import org.da.expressionj.expr.parser.ParseException;
import org.da.expressionj.model.Equation;

import com.fyxridd.lib.show.chat.api.condition.Condition;
import com.fyxridd.lib.show.chat.api.condition.Convertable;

/**
 * 数学比较
 */
public class MathCompareCondition implements Condition, Convertable{
    private String exprStr;
    private Equation expr;

    //performance
    //true表示expr需要更新,false表示不需要更新
    private boolean updateFlag;
    
    public MathCompareCondition(String exprStr) {
        super();
        this.exprStr = exprStr;
        this.updateFlag = true;
    }

    private MathCompareCondition(Equation expr) {
        super();
        this.expr = expr;
    }
    
    @Override
    public boolean check() {
        //使用前先更新
        update();
        //检测
        return expr.evalAsBoolean();
    }

    /**
     * 检测更新
     */
    private void update() {
        if (!updateFlag) return;
        updateFlag = false;
        try {
            expr = EquationParser.parse(exprStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MathCompareCondition clone(){
        return new MathCompareCondition(expr);
    }

    @Override
    public void convert(String from, Object to) {
        exprStr = exprStr.replace(from, to.toString());
        updateFlag = true;
    }

    @Override
    public String getConvertedStr() {
        return exprStr;
    }

    /**
     * 转换变量
     */
    public void convertVariable(String name, Object value) {
        expr.setValue(name, value);
    }


    /**
     * 从字符串中读取条件
     * @return 异常返回null
     */
    public static MathCompareCondition loadFromString(String condition) {
        return new MathCompareCondition(condition);
    }

    /**
     * 保存到字符串
     */
    public static String saveToString(MathCompareCondition condition) {
        return condition.exprStr;
    }
}
