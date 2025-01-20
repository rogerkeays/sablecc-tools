import java.util.*;
import java.io.*;
import org.sablecc.sablecc2x.analysis.*;
import org.sablecc.sablecc2x.lexer.*;
import org.sablecc.sablecc2x.parser.*;
import org.sablecc.sablecc2x.node.*;


/**
 * Walker to generate latex version of a BNF grammar which uses the bnf tex
 * package.  
 *
 * Author: Roger Keays <r.keays@ninthave.net>
 * Date: 7/10/2001
 */
public class BNFPrinter extends DepthFirstAdapter
{

	/* header stuff */
	public void inStart(Start node)
	{
		System.out.println
			("% \n" +
			 "% This file is automatically generated \n" +
			 "% \n" +
			 "\\documentclass{article} \n" +
			 "\\usepackage{bnf} \n" +
			 "\\begin{document} \n" +
			 "\\begin{bnf} \n" +
			 "\t\\begin{eqnarray*}");
	}

	/* into a production */
	public void inAProd(AProd node)
    {
		System.out.print ("\t\t<" + node.getId().getText() + ">& -> &");
    }

	/* out of a single alternative */
	public void outAParsedAlt(AParsedAlt node)
    {
		System.out.print (" \\\\ \n");
    }
	
	/* into an alternative (except the first) */
	public void inAAltsTail(AAltsTail node)
    {
		/* do not print out ignored alternatives */
		if (node.getAlt() instanceof AIgnoredAlt) return;

		System.out.print ("\t\t\t &&| ");
    }

	/* out of an element TODO: distinguish between production specifiers
	   and token specifiers */
    public void outAElem(AElem node)
    {

		/* do not print out ignored alternatives */
		if (node.parent() instanceof AIgnoredAlt) return;

		PUnOp opNode = node.getUnOp();
		String op = "";
		String id = node.getId().getText();
	
		if (opNode instanceof AStarUnOp) op = "*";
		else if (opNode instanceof AQMarkUnOp) op = "?";
		else if (opNode instanceof APlusUnOp) op = "+";
		System.out.print ("<" + id + op + "> ");
    }

	/* footer stuff */
	public void outStart(Start node)
	{
		System.out.println
			("\t\\end{eqnarray*}\n" +
			 "\\end{bnf} \n" +
			 "\\end{document}");		
	}

	/* fire up the compiler */
	public static void main(String [] args) 
	{

		try {
			Parser p = new Parser
				(new Lexer
					(new PushbackReader
						(new InputStreamReader(System.in), 1024)));

			Start tree = p.parse();
			tree.apply(new BNFPrinter());
		} catch (Exception e) {

			System.out.println(e.getMessage());
		}
	}
}

