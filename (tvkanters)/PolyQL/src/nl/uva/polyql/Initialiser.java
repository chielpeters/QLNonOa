package nl.uva.polyql;

import java.io.FileInputStream;
import java.io.IOException;

import nl.uva.polyql.antlr4.LineInfoListener;
import nl.uva.polyql.antlr4.QuestionnaireLexer;
import nl.uva.polyql.antlr4.QuestionnaireParser;
import nl.uva.polyql.antlr4.QuestionnaireParser.FormContext;
import nl.uva.polyql.ast.Rule;
import nl.uva.polyql.ast.RuleContainer;
import nl.uva.polyql.utils.Log;
import nl.uva.polyql.validation.Validator;
import nl.uva.polyql.view.FormFrame;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class Initialiser {

    public static void main(final String... params) {

        Log.setInfoLogging(false);

        try {
            final ANTLRInputStream input = new ANTLRInputStream(new FileInputStream("res/exampleform.txt"));
            final QuestionnaireLexer lexer = new QuestionnaireLexer(input);
            final CommonTokenStream tokens = new CommonTokenStream(lexer);
            final QuestionnaireParser parser = new QuestionnaireParser(tokens);
            parser.addParseListener(new LineInfoListener());
            final FormContext tree = parser.form();

            Log.i("==========================");

            Log.i(tree.toStringTree(parser));

            Log.i("==========================");

            printRuleContainer(tree.f, 0);

            final Validator validator = Validator.validate(tree.f);
            validator.print();
            if (!validator.isFatal()) {
                new FormFrame(tree.f);
            }

        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void printRuleContainer(final RuleContainer form, final int level) {
        String indent = "";
        for (int i = 0; i < level * 4; ++i) {
            indent += " ";
        }

        for (final Rule rule : form.getRules()) {
            Log.i(indent + rule);

            if (rule instanceof RuleContainer) {
                printRuleContainer((RuleContainer) rule, level + 1);
            }
        }

    }
}
