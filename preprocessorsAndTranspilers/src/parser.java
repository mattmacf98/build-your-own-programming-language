//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";



public class parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt;
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class parserVal is defined in parserVal.java


String   yytext;//user variable to return contextual strings
parserVal yyval; //used to return semantic vals from action routines
parserVal yylval;//the 'lval' (result) I got from yylex()
parserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new parserVal[YYSTACKSIZE];
  yyval=new parserVal();
  yylval=new parserVal();
  valptr=-1;
}
void val_push(parserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
parserVal val_pop()
{
  if (valptr<0)
    return new parserVal();
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
parserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new parserVal();
  return valstk[ptr];
}
final parserVal dup_yyval(parserVal val)
{
  parserVal dup = new parserVal();
  dup.ival = val.ival;
  dup.dval = val.dval;
  dup.sval = val.sval;
  dup.obj = val.obj;
  return dup;
}
//#### end semantic value section ####
public final static short BREAK=257;
public final static short DOUBLE=258;
public final static short ELSE=259;
public final static short FOR=260;
public final static short IF=261;
public final static short INT=262;
public final static short RETURN=263;
public final static short VOID=264;
public final static short WHILE=265;
public final static short IDENTIFIER=266;
public final static short CLASSNAME=267;
public final static short CLASS=268;
public final static short STRING=269;
public final static short BOOL=270;
public final static short INTLIT=271;
public final static short DOUBLELIT=272;
public final static short STRINGLIT=273;
public final static short BOOLLIT=274;
public final static short NULLVAL=275;
public final static short LESSTHANOREQUAL=276;
public final static short GREATERTHANOREQUAL=277;
public final static short ISEQUALTO=278;
public final static short NOTEQUALTO=279;
public final static short LOGICALAND=280;
public final static short LOGICALOR=281;
public final static short INCREMENT=282;
public final static short DECREMENT=283;
public final static short PUBLIC=284;
public final static short STATIC=285;
public final static short NEW=286;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    1,    1,    2,    2,    3,    3,    3,    4,    7,
    7,    7,    7,    7,    9,    9,   10,    8,    8,   11,
   11,   12,   12,    5,   13,   15,   16,   16,   17,   17,
   18,    6,   14,   19,   19,   20,   20,   21,   21,   22,
   24,   23,   23,   23,   23,   23,   23,   23,   23,   23,
   23,   25,   33,   33,   28,   29,   30,   30,   37,   37,
   38,   31,   32,   39,   39,   39,   40,   40,   41,   41,
   42,   42,   26,   26,   27,   43,   43,   43,   43,   43,
   43,   43,   47,   48,   44,   44,   44,   44,   44,   50,
   50,   45,   49,   49,   35,   35,   51,   51,   52,   52,
   52,   53,   53,   53,   53,   54,   54,   54,   55,   55,
   55,   55,   56,   56,   57,   57,   57,   58,   58,   59,
   59,   36,   36,   34,   60,   60,   60,   61,   61,   61,
   46,
};
final static short yylen[] = {                            2,
    4,    3,    2,    1,    2,    1,    1,    1,    3,    1,
    1,    1,    1,    1,    1,    1,    3,    1,    3,    1,
    3,    1,    1,    2,    4,    4,    1,    0,    1,    3,
    2,    2,    3,    1,    0,    1,    2,    1,    1,    2,
    2,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    2,    1,    1,    5,    7,    6,    8,    1,    2,
    2,    5,    9,    1,    1,    0,    1,    0,    1,    0,
    1,    3,    2,    3,    3,    1,    1,    1,    1,    3,
    1,    1,    5,    5,    1,    1,    1,    1,    1,    1,
    3,    3,    1,    0,    4,    6,    1,    1,    2,    2,
    1,    1,    3,    3,    3,    1,    3,    3,    1,    1,
    1,    1,    1,    3,    1,    3,    3,    1,    3,    1,
    3,    1,    1,    3,    1,    1,    1,    1,    1,    1,
    4,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    0,    1,   11,   10,    0,   13,
   12,    0,    3,    0,    4,    6,    7,    8,    0,    0,
   16,    0,    0,    0,    0,    2,    5,   20,    0,    0,
    0,    0,   24,   32,   15,    0,    0,    0,   29,   23,
   22,    0,    9,    0,    0,   17,    0,    0,    0,    0,
    0,   85,   86,   88,   87,   89,    0,   43,    0,    0,
    0,   42,    0,    0,   36,   38,   39,    0,   44,   45,
   46,   47,   48,   49,   50,   51,    0,   53,    0,    0,
   76,    0,    0,   81,   82,    0,    0,   26,    0,    0,
   25,    0,   21,    0,   73,    0,    0,    0,    0,    0,
  123,   78,   67,    0,    0,  101,  102,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   33,   37,   40,   52,    0,  129,  130,  128,    0,   30,
   74,   65,   71,    0,    0,    0,    0,   77,   79,   99,
  100,   75,    0,    0,    0,    0,    0,  109,  110,  111,
  112,    0,    0,    0,    0,    0,    0,    0,    0,   80,
    0,   90,    0,    0,    0,  124,    0,    0,    0,  103,
  104,  105,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  131,   95,    0,    0,    0,    0,   72,    0,
   62,   83,   84,   91,    0,    0,    0,    0,   59,   96,
    0,    0,    0,   56,   61,    0,   60,    0,    0,   58,
   63,    0,    0,   55,
};
final static short yydgoto[] = {                          2,
    6,   14,   15,   16,   17,   18,   60,   29,  100,   21,
   30,   42,   22,   62,   23,   37,   38,   39,   63,   64,
   65,   66,   67,   68,   69,   70,   71,   72,   73,   74,
   75,   76,   77,  101,  102,  162,  198,  199,  134,  104,
  201,  135,  105,   81,   82,   83,   84,   85,  163,  164,
  106,  107,  108,  109,  152,  110,  111,  112,  113,   86,
  129,
};
final static short yysindex[] = {                      -279,
 -259,    0, -248,  -95,  -79,    0,    0,    0,    1,    0,
    0, -218,    0,  273,    0,    0,    0,    0, -194,   45,
    0,  -29,  -29, -146,  255,    0,    0,    0,  -17,   16,
 -152,  113,    0,    0,    0, -194,   76,   86,    0,    0,
    0, -124,    0, -194,   55,    0,  -57,  123,  127,  -33,
  128,    0,    0,    0,    0,    0, -146,    0,  -33, -194,
   73,    0,   44,  113,    0,    0,    0,   95,    0,    0,
    0,    0,    0,    0,    0,    0,  112,    0,    0,  129,
    0,    0,    0,    0,    0,  -60,   16,    0, -146,    1,
    0,   16,    0,  118,    0,  158,  -33,  -33,  -33,   73,
    0,    0,    0,  119,  129,    0,    0,   96,  117,    4,
 -234,  -98,  -97,  -33,   94,  100,  145,  144,  -33,  -33,
    0,    0,    0,    0,  -77,    0,    0,    0,  -33,    0,
    0,    0,    0,  134,  151,  156,   73,    0,    0,    0,
    0,    0,  -33,  -33,  -33,  -33,  -33,    0,    0,    0,
    0,  -33,  -33,  -33,  -33,  -33,  167,  -33,  -33,    0,
  120,    0,  169,  168,  171,    0,  -33,   13,  -29,    0,
    0,    0,   96,   96,  117,    4,    4, -234,  -98,  140,
  124,  179,    0,    0,  -33,  -33,  178,   73,    0,  -14,
    0,    0,    0,    0,  185,   13, -120,  -12,    0,    0,
  189,  151,  222,    0,    0, -120,    0,  -29,  -33,    0,
    0,  223,  -29,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    0,  -42,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    7,
    0,    0,    0,  233,    0,    0,    0,    0,    0,   10,
    0,  150,    0,    0,    0,    0,    0,  235,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  219,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  -51,    0,    0,  157,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   93,    0,
    0,  -22,  -11,    0,    0,    0,   24,    0,    0,    0,
    0,   46,    0,    0,    0,  224,    0,    0,    0,   42,
    0,    0,    0,    0,  114,    0,    0,  184,  427,  -30,
   56,   52,   51,    0,    0,  198,    0,  231,    0,  259,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  242,    0,  159,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  259,    0,
    0,    0,    0,  261,   15,    0,  219,    0,    0,    0,
    0,    0,  407,  413,  433,   29,  366,   88,   77,    0,
    0,    0,    0,    0,    0,  259,    0,  -48,    0,   69,
    0,    0,    0,    0,    0,  262,    0,   91,    0,    0,
    0,  263,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    0,    0,  291,    0,    0,    0,  483,  246,  480,    0,
   11,    0,    0,  358,  269,    0,    0,  225,    0,    0,
  248,    0,  133,  220,    0,    0,    0, -189,    0,    0,
    0,    0,  -90,  385,  414,  432,    0,  130,    0,  148,
    0,  121,  415,    0,  460,  510,    0,    0, -143,    0,
    0,  -18,   19,  193,    0,  -55,  191,  194,    0,    0,
    0,
};
final static int YYTABLESIZE=714;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         99,
  128,   95,   32,   15,    1,  133,   59,  205,    3,  125,
  115,   98,  125,  115,   77,  182,  205,    4,   77,   77,
   77,   77,   77,   77,   77,   79,   44,    5,  115,   79,
   79,   79,   79,   79,   79,   79,   77,   77,  126,   77,
   24,   43,  195,  153,  154,   13,   87,   79,   79,  127,
   79,   92,   59,   18,   92,   92,   92,   92,   92,   92,
   92,   92,  115,  150,   31,  151,   25,   31,   18,  116,
   77,   28,  116,   92,   92,   92,   92,  189,   98,  140,
  141,   79,   98,   98,   98,   98,   98,  116,   98,   19,
   31,  122,  120,   32,  122,  120,  118,  176,  177,  118,
   98,   98,  125,   98,   19,  133,   45,   92,   55,  122,
  120,    7,  120,   46,  118,    8,   88,  121,   31,   35,
  121,  116,   10,   11,  170,  171,  172,   55,  119,   89,
   57,  119,  145,   54,   98,  121,   54,  143,   78,  159,
  203,   90,  144,  122,  120,   31,  119,   93,  118,   57,
   97,   54,   59,  123,   97,   97,   97,   97,   97,  147,
   97,  146,   96,  119,  173,  174,   97,  114,  121,  121,
  124,   58,   97,   97,  125,   97,  131,  142,    7,   59,
  119,  155,    8,  156,  158,  160,    9,   44,  165,   10,
   11,   55,  167,   55,  168,   98,  169,   59,   58,   98,
   98,   98,   98,   98,   12,   98,   97,  180,   94,  184,
  186,  185,  183,   57,   14,   57,  192,   98,   98,  193,
   98,  126,  127,   15,  106,  200,  106,  106,  106,  208,
  125,  125,   35,  125,  125,   32,  196,   52,   53,   54,
   55,   56,  106,  106,  197,  106,  206,  115,  115,  115,
  115,   98,   57,   77,   77,   77,   77,   77,   77,  126,
  126,  209,   32,  213,   79,   79,   79,   79,   79,   79,
  127,  127,   14,   28,   35,   27,  106,   68,   35,  148,
  149,   34,   66,   52,   53,   54,   55,   56,   14,   41,
   92,   92,   92,   92,   92,   92,   92,   92,   57,   94,
   64,   93,   70,   69,   27,  118,  116,  116,  116,  116,
   91,  122,  191,  130,  187,  132,  202,   98,   98,   98,
   98,   98,   98,  125,  125,   55,   55,  207,   55,   55,
   55,   55,  120,   55,   55,  118,  118,   55,   55,   55,
   55,   55,   55,   55,  175,  178,    0,   57,   57,  179,
   57,   57,   57,   57,   55,   57,   57,  121,    0,   57,
   57,   57,   57,   57,   57,   57,    0,  119,  119,   47,
    7,    0,   48,   49,    8,   50,   57,   51,   35,   33,
   34,   10,   11,   52,   53,   54,   55,   56,    0,   97,
   97,   97,   97,   97,   97,    0,   47,   26,   57,   48,
   49,    0,   50,    0,   51,   35,  117,    0,    0,  117,
   52,   53,   54,   55,   56,    7,   78,    0,    0,    8,
    0,    0,    0,   35,  117,   57,   10,   11,   52,   53,
   54,   55,   56,    0,   98,   98,   98,   98,   98,   98,
    0,    0,    0,   57,    0,   79,   80,  108,   78,  108,
  108,  108,    0,  107,    0,  107,  107,  107,  117,  106,
  106,  106,  106,  106,  106,  108,  108,  113,  108,    0,
  113,  107,  107,  114,  107,    0,  114,   79,   80,    0,
   78,  103,    0,    0,   20,  113,  113,   19,  113,    0,
  117,  114,  114,   20,  114,    0,   19,    0,    0,  108,
    0,    0,    0,   20,   20,  107,   36,   41,    0,   79,
   80,   61,    7,    0,    0,    0,    8,    0,   40,  113,
   35,    0,    0,   10,   11,  114,  190,    0,  136,    0,
    7,    0,    0,    0,    8,    0,  116,    0,    9,  115,
    0,   10,   11,   61,    0,  157,    0,    0,    0,    0,
  161,    0,   78,    0,  204,    0,   12,  138,  138,    0,
  166,    0,    0,  210,   78,  211,    0,    0,   20,    0,
  214,   36,    0,    0,    0,   61,    0,  137,  137,    0,
   78,   79,   80,    0,    0,    0,    0,    0,    0,  181,
    0,    0,    0,   79,   80,    0,    0,    0,  103,    0,
    0,    0,  138,  138,  138,  138,  138,  139,  139,   79,
   80,  138,  138,  138,  138,  138,  194,    0,    0,    0,
    0,    0,  137,  137,  137,  137,  137,    0,    0,    0,
    0,  137,  137,  137,  137,  137,    0,    0,    0,    0,
  212,    0,    0,  117,  117,  117,  117,  188,    0,    0,
    0,    0,  139,  139,  139,  139,  139,    0,    0,  188,
    0,  139,  139,  139,  139,  139,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  188,    0,    0,    0,    0,
    0,    0,  108,  108,  108,  108,  108,  108,  107,  107,
  107,  107,  107,  107,    0,    0,    0,    0,    0,    0,
    0,    0,  113,  113,  113,  113,  113,  113,  114,  114,
  114,  114,  114,  114,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         33,
   61,   59,  123,   46,  284,   96,   40,  197,  268,   61,
   41,   45,   61,   44,   37,  159,  206,  266,   41,   42,
   43,   44,   45,   46,   47,   37,   44,  123,   59,   41,
   42,   43,   44,   45,   46,   47,   59,   60,   61,   62,
   40,   59,  186,  278,  279,  125,   36,   59,   60,   61,
   62,   37,   40,   44,   44,   41,   42,   43,   44,   45,
   46,   47,   93,   60,   41,   62,  285,   44,   59,   41,
   93,  266,   44,   59,   60,   61,   62,  168,   37,   98,
   99,   93,   41,   42,   43,   44,   45,   59,   47,   44,
   46,   41,   41,  123,   44,   44,   41,  153,  154,   44,
   59,   60,   61,   62,   59,  196,   91,   93,   40,   59,
   59,  258,   40,  266,   59,  262,   41,   41,   46,  266,
   44,   93,  269,  270,  143,  144,  145,   59,   41,   44,
   40,   44,   37,   41,   93,   59,   44,   42,   46,   40,
  261,  266,   47,   93,   93,   46,   59,   93,   93,   59,
   37,   59,   40,   59,   41,   42,   43,   44,   45,   43,
   47,   45,   40,   91,  146,  147,   40,   40,  125,   93,
   59,   59,   59,   60,   46,   62,   59,   59,  258,   40,
   93,  280,  262,  281,   91,   41,  266,   44,  266,  269,
  270,  123,   59,  125,   44,   37,   41,   40,   59,   41,
   42,   43,   44,   45,  284,   47,   93,   41,  266,   41,
   40,   44,   93,  123,  266,  125,   93,   59,   60,   41,
   62,  282,  283,  266,   41,   41,   43,   44,   45,   41,
  282,  283,  266,  282,  283,  123,   59,  271,  272,  273,
  274,  275,   59,   60,  259,   62,  259,  278,  279,  280,
  281,   93,  286,  276,  277,  278,  279,  280,  281,  282,
  283,   40,  123,   41,  276,  277,  278,  279,  280,  281,
  282,  283,  266,   41,  125,   41,   93,   59,  266,  276,
  277,  125,   59,  271,  272,  273,  274,  275,   91,   59,
  276,  277,  278,  279,  280,  281,  282,  283,  286,   41,
   59,   41,   41,   41,   14,   60,  278,  279,  280,  281,
   42,   64,  180,   89,  167,   96,  196,  276,  277,  278,
  279,  280,  281,  282,  283,  257,  258,  198,  260,  261,
  262,  263,  281,  265,  266,  280,  281,  269,  270,  271,
  272,  273,  274,  275,  152,  155,   -1,  257,  258,  156,
  260,  261,  262,  263,  286,  265,  266,  281,   -1,  269,
  270,  271,  272,  273,  274,  275,   -1,  280,  281,  257,
  258,   -1,  260,  261,  262,  263,  286,  265,  266,   22,
   23,  269,  270,  271,  272,  273,  274,  275,   -1,  276,
  277,  278,  279,  280,  281,   -1,  257,  125,  286,  260,
  261,   -1,  263,   -1,  265,  266,   41,   -1,   -1,   44,
  271,  272,  273,  274,  275,  258,   32,   -1,   -1,  262,
   -1,   -1,   -1,  266,   59,  286,  269,  270,  271,  272,
  273,  274,  275,   -1,  276,  277,  278,  279,  280,  281,
   -1,   -1,   -1,  286,   -1,   32,   32,   41,   64,   43,
   44,   45,   -1,   41,   -1,   43,   44,   45,   93,  276,
  277,  278,  279,  280,  281,   59,   60,   41,   62,   -1,
   44,   59,   60,   41,   62,   -1,   44,   64,   64,   -1,
   96,   50,   -1,   -1,    5,   59,   60,    5,   62,   -1,
   59,   59,   60,   14,   62,   -1,   14,   -1,   -1,   93,
   -1,   -1,   -1,   24,   25,   93,   24,   25,   -1,   96,
   96,   32,  258,   -1,   -1,   -1,  262,   -1,  264,   93,
  266,   -1,   -1,  269,  270,   93,  169,   -1,   97,   -1,
  258,   -1,   -1,   -1,  262,   -1,   57,   -1,  266,   57,
   -1,  269,  270,   64,   -1,  114,   -1,   -1,   -1,   -1,
  119,   -1,  168,   -1,  197,   -1,  284,   98,   99,   -1,
  129,   -1,   -1,  206,  180,  208,   -1,   -1,   89,   -1,
  213,   89,   -1,   -1,   -1,   96,   -1,   98,   99,   -1,
  196,  168,  168,   -1,   -1,   -1,   -1,   -1,   -1,  158,
   -1,   -1,   -1,  180,  180,   -1,   -1,   -1,  167,   -1,
   -1,   -1,  143,  144,  145,  146,  147,   98,   99,  196,
  196,  152,  153,  154,  155,  156,  185,   -1,   -1,   -1,
   -1,   -1,  143,  144,  145,  146,  147,   -1,   -1,   -1,
   -1,  152,  153,  154,  155,  156,   -1,   -1,   -1,   -1,
  209,   -1,   -1,  278,  279,  280,  281,  168,   -1,   -1,
   -1,   -1,  143,  144,  145,  146,  147,   -1,   -1,  180,
   -1,  152,  153,  154,  155,  156,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  196,   -1,   -1,   -1,   -1,
   -1,   -1,  276,  277,  278,  279,  280,  281,  276,  277,
  278,  279,  280,  281,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  276,  277,  278,  279,  280,  281,  276,  277,
  278,  279,  280,  281,
};
}
final static short YYFINAL=2;
final static short YYMAXTOKEN=286;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,"'!'",null,null,null,"'%'",null,null,"'('","')'","'*'","'+'",
"','","'-'","'.'","'/'",null,null,null,null,null,null,null,null,null,null,null,
"';'","'<'","'='","'>'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,"'['",null,"']'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,"'{'",null,"'}'",null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,"BREAK","DOUBLE","ELSE","FOR","IF",
"INT","RETURN","VOID","WHILE","IDENTIFIER","CLASSNAME","CLASS","STRING","BOOL",
"INTLIT","DOUBLELIT","STRINGLIT","BOOLLIT","NULLVAL","LESSTHANOREQUAL",
"GREATERTHANOREQUAL","ISEQUALTO","NOTEQUALTO","LOGICALAND","LOGICALOR",
"INCREMENT","DECREMENT","PUBLIC","STATIC","NEW",
};
final static String yyrule[] = {
"$accept : ClassDecl",
"ClassDecl : PUBLIC CLASS IDENTIFIER ClassBody",
"ClassBody : '{' ClassBodyDecls '}'",
"ClassBody : '{' '}'",
"ClassBodyDecls : ClassBodyDecl",
"ClassBodyDecls : ClassBodyDecls ClassBodyDecl",
"ClassBodyDecl : FieldDecl",
"ClassBodyDecl : MethodDecl",
"ClassBodyDecl : ConstructorDecl",
"FieldDecl : Type VarDecls ';'",
"Type : INT",
"Type : DOUBLE",
"Type : BOOL",
"Type : STRING",
"Type : Name",
"Name : IDENTIFIER",
"Name : QualifiedName",
"QualifiedName : Name '.' IDENTIFIER",
"VarDecls : VarDeclarator",
"VarDecls : VarDecls ',' VarDeclarator",
"VarDeclarator : IDENTIFIER",
"VarDeclarator : VarDeclarator '[' ']'",
"MethodReturnVal : Type",
"MethodReturnVal : VOID",
"MethodDecl : MethodHeader Block",
"MethodHeader : PUBLIC STATIC MethodReturnVal MethodDeclarator",
"MethodDeclarator : IDENTIFIER '(' FormalParmListOpt ')'",
"FormalParmListOpt : FormalParmList",
"FormalParmListOpt :",
"FormalParmList : FormalParm",
"FormalParmList : FormalParmList ',' FormalParm",
"FormalParm : Type VarDeclarator",
"ConstructorDecl : MethodDeclarator Block",
"Block : '{' BlockStmtsOpt '}'",
"BlockStmtsOpt : BlockStmts",
"BlockStmtsOpt :",
"BlockStmts : BlockStmt",
"BlockStmts : BlockStmts BlockStmt",
"BlockStmt : LocalVarDeclStmt",
"BlockStmt : Stmt",
"LocalVarDeclStmt : LocalVarDecl ';'",
"LocalVarDecl : Type VarDecls",
"Stmt : Block",
"Stmt : ';'",
"Stmt : ExprStmt",
"Stmt : BreakStmt",
"Stmt : ReturnStmt",
"Stmt : IfThenStmt",
"Stmt : IfThenElseStmt",
"Stmt : IfThenElseIfStmt",
"Stmt : WhileStmt",
"Stmt : ForStmt",
"ExprStmt : StmtExpr ';'",
"StmtExpr : Assignment",
"StmtExpr : MethodCall",
"IfThenStmt : IF '(' Expr ')' Block",
"IfThenElseStmt : IF '(' Expr ')' Block ELSE Block",
"IfThenElseIfStmt : IF '(' Expr ')' Block ElseIfSequence",
"IfThenElseIfStmt : IF '(' Expr ')' Block ElseIfSequence ELSE Block",
"ElseIfSequence : ElseIfStmt",
"ElseIfSequence : ElseIfSequence ElseIfStmt",
"ElseIfStmt : ELSE IfThenStmt",
"WhileStmt : WHILE '(' Expr ')' Stmt",
"ForStmt : FOR '(' ForInit ';' ExprOpt ';' ForUpdate ')' Block",
"ForInit : StmtExprList",
"ForInit : LocalVarDecl",
"ForInit :",
"ExprOpt : Expr",
"ExprOpt :",
"ForUpdate : StmtExprList",
"ForUpdate :",
"StmtExprList : StmtExpr",
"StmtExprList : StmtExprList ',' StmtExpr",
"BreakStmt : BREAK ';'",
"BreakStmt : BREAK IDENTIFIER ';'",
"ReturnStmt : RETURN ExprOpt ';'",
"Primary : Literal",
"Primary : FieldAccess",
"Primary : MethodCall",
"Primary : ArrayAccess",
"Primary : '(' Expr ')'",
"Primary : ArrayCreation",
"Primary : InstanceCreation",
"ArrayCreation : NEW Type '[' Expr ']'",
"InstanceCreation : NEW Name '(' ArgListOpt ')'",
"Literal : INTLIT",
"Literal : DOUBLELIT",
"Literal : BOOLLIT",
"Literal : STRINGLIT",
"Literal : NULLVAL",
"ArgList : Expr",
"ArgList : ArgList ',' Expr",
"FieldAccess : Primary '.' IDENTIFIER",
"ArgListOpt : ArgList",
"ArgListOpt :",
"MethodCall : Name '(' ArgListOpt ')'",
"MethodCall : Primary '.' IDENTIFIER '(' ArgListOpt ')'",
"PostFixExpr : Primary",
"PostFixExpr : Name",
"UnaryExpr : '-' UnaryExpr",
"UnaryExpr : '!' UnaryExpr",
"UnaryExpr : PostFixExpr",
"MulExpr : UnaryExpr",
"MulExpr : MulExpr '*' UnaryExpr",
"MulExpr : MulExpr '/' UnaryExpr",
"MulExpr : MulExpr '%' UnaryExpr",
"AddExpr : MulExpr",
"AddExpr : AddExpr '+' MulExpr",
"AddExpr : AddExpr '-' MulExpr",
"RelOp : LESSTHANOREQUAL",
"RelOp : GREATERTHANOREQUAL",
"RelOp : '<'",
"RelOp : '>'",
"RelExpr : AddExpr",
"RelExpr : RelExpr RelOp AddExpr",
"EqExpr : RelExpr",
"EqExpr : EqExpr ISEQUALTO RelExpr",
"EqExpr : EqExpr NOTEQUALTO RelExpr",
"CondAndExpr : EqExpr",
"CondAndExpr : CondAndExpr LOGICALAND EqExpr",
"CondOrExpr : CondAndExpr",
"CondOrExpr : CondOrExpr LOGICALOR CondAndExpr",
"Expr : CondOrExpr",
"Expr : Assignment",
"Assignment : LeftHandSide AssignOp Expr",
"LeftHandSide : Name",
"LeftHandSide : FieldAccess",
"LeftHandSide : ArrayAccess",
"AssignOp : '='",
"AssignOp : INCREMENT",
"AssignOp : DECREMENT",
"ArrayAccess : Name '[' Expr ']'",
};

//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop");
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = j0.yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror.yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror.yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror.yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 8 "j0gram.y"
{
  yyval=j0.node("ClassDecl",1000,val_peek(1),val_peek(0));
  j0.print(yyval);
  j0.semantic(yyval);
  j0.generateUnicon(yyval);
 }
break;
case 2:
//#line 14 "j0gram.y"
{ yyval=j0.node("ClassBody",1010,val_peek(1)); }
break;
case 3:
//#line 15 "j0gram.y"
{ yyval=j0.node("ClassBody",1011); }
break;
case 5:
//#line 17 "j0gram.y"
{
  yyval=j0.node("ClassBodyDecls",1020,val_peek(1),val_peek(0)); }
break;
case 9:
//#line 20 "j0gram.y"
{
  yyval=j0.node("FieldDecl",1030,val_peek(2),val_peek(1));
  j0.calcType(yyval);}
break;
case 17:
//#line 26 "j0gram.y"
{
  yyval=j0.node("QualifiedName",1040,val_peek(2),val_peek(0));}
break;
case 19:
//#line 29 "j0gram.y"
{
  yyval=j0.node("VarDecls",1050,val_peek(2),val_peek(0)); }
break;
case 21:
//#line 31 "j0gram.y"
{
  yyval=j0.node("VarDeclarator",1060,val_peek(2)); }
break;
case 24:
//#line 35 "j0gram.y"
{
  yyval=j0.node("MethodDecl",1380,val_peek(1),val_peek(0));
 }
break;
case 25:
//#line 38 "j0gram.y"
{
  yyval=j0.node("MethodHeader",1070,val_peek(1),val_peek(0));
  j0.calcType(yyval);}
break;
case 26:
//#line 41 "j0gram.y"
{
  yyval=j0.node("MethodDeclarator",1080,val_peek(3),val_peek(1)); }
break;
case 30:
//#line 45 "j0gram.y"
{
  yyval=j0.node("FormalParmList",1090,val_peek(2),val_peek(0)); }
break;
case 31:
//#line 47 "j0gram.y"
{
  yyval=j0.node("FormalParm",1100,val_peek(1),val_peek(0));
  j0.calcType(yyval);
 }
break;
case 32:
//#line 52 "j0gram.y"
{
  yyval=j0.node("ConstructorDecl",1110,val_peek(1),val_peek(0)); }
break;
case 33:
//#line 55 "j0gram.y"
{yyval=j0.node("Block",1200,val_peek(1));}
break;
case 37:
//#line 57 "j0gram.y"
{
  yyval=j0.node("BlockStmts",1130,val_peek(1),val_peek(0)); }
break;
case 41:
//#line 62 "j0gram.y"
{
  yyval=j0.node("LocalVarDecl",1140,val_peek(1),val_peek(0));
  j0.calcType(yyval);}
break;
case 55:
//#line 74 "j0gram.y"
{
  yyval=j0.node("IfThenStmt",1150,val_peek(2),val_peek(0)); }
break;
case 56:
//#line 76 "j0gram.y"
{
  yyval=j0.node("IfThenElseStmt",1160,val_peek(4),val_peek(2),val_peek(0)); }
break;
case 57:
//#line 78 "j0gram.y"
{
  yyval=j0.node("IfThenElseIfStmt",1170,val_peek(3),val_peek(1),val_peek(0)); }
break;
case 58:
//#line 80 "j0gram.y"
{
  yyval=j0.node("IfThenElseIfStmt",1171,val_peek(5),val_peek(3),val_peek(2),val_peek(0)); }
break;
case 60:
//#line 83 "j0gram.y"
{
  yyval=j0.node("ElseIfSequence",1180,val_peek(1),val_peek(0)); }
break;
case 61:
//#line 85 "j0gram.y"
{
  yyval=j0.node("ElseIfStmt",1190,val_peek(0)); }
break;
case 62:
//#line 87 "j0gram.y"
{
  yyval=j0.node("WhileStmt",1210,val_peek(2),val_peek(0)); }
break;
case 63:
//#line 90 "j0gram.y"
{
  yyval=j0.node("ForStmt",1220,val_peek(6),val_peek(4),val_peek(2),val_peek(0)); }
break;
case 72:
//#line 96 "j0gram.y"
{
  yyval=j0.node("StmtExprList",1230,val_peek(2),val_peek(0)); }
break;
case 74:
//#line 99 "j0gram.y"
{
  yyval=j0.node("BreakStmt",1240,val_peek(1)); }
break;
case 75:
//#line 101 "j0gram.y"
{
  yyval=j0.node("ReturnStmt",1250,val_peek(1)); }
break;
case 80:
//#line 104 "j0gram.y"
{
  yyval=val_peek(1);}
break;
case 83:
//#line 106 "j0gram.y"
{
  yyval=j0.node("ArrayCreation", 1260, val_peek(3), val_peek(1));}
break;
case 84:
//#line 108 "j0gram.y"
{
  yyval=j0.node("InstanceCreation", 1261, val_peek(3), val_peek(1));}
break;
case 91:
//#line 112 "j0gram.y"
{
  yyval=j0.node("ArgList",1270,val_peek(2),val_peek(0)); }
break;
case 92:
//#line 114 "j0gram.y"
{
  yyval=j0.node("FieldAccess",1280,val_peek(2),val_peek(0)); }
break;
case 95:
//#line 118 "j0gram.y"
{
  yyval=j0.node("MethodCall",1290,val_peek(3),val_peek(1)); }
break;
case 96:
//#line 120 "j0gram.y"
{
    yyval=j0.node("MethodCall",1291,val_peek(5),val_peek(3),val_peek(1)); }
break;
case 99:
//#line 125 "j0gram.y"
{
  yyval=j0.node("UnaryExpr",1300,val_peek(1),val_peek(0)); }
break;
case 100:
//#line 127 "j0gram.y"
{
  yyval=j0.node("UnaryExpr",1301,val_peek(1),val_peek(0)); }
break;
case 103:
//#line 131 "j0gram.y"
{
      yyval=j0.node("MulExpr",1310,val_peek(2),val_peek(0)); }
break;
case 104:
//#line 133 "j0gram.y"
{
      yyval=j0.node("MulExpr",1311,val_peek(2),val_peek(0)); }
break;
case 105:
//#line 135 "j0gram.y"
{
      yyval=j0.node("MulExpr",1312,val_peek(2),val_peek(0)); }
break;
case 107:
//#line 138 "j0gram.y"
{
      yyval=j0.node("AddExpr",1320,val_peek(2),val_peek(0)); }
break;
case 108:
//#line 140 "j0gram.y"
{
      yyval=j0.node("AddExpr",1321,val_peek(2),val_peek(0)); }
break;
case 114:
//#line 143 "j0gram.y"
{
  yyval=j0.node("RelExpr",1330,val_peek(2),val_peek(1),val_peek(0)); }
break;
case 116:
//#line 147 "j0gram.y"
{
  yyval=j0.node("EqExpr",1340,val_peek(2),val_peek(0)); }
break;
case 117:
//#line 149 "j0gram.y"
{
  yyval=j0.node("EqExpr",1341,val_peek(2),val_peek(0)); }
break;
case 119:
//#line 151 "j0gram.y"
{
  yyval=j0.node("CondAndExpr", 1350, val_peek(2), val_peek(0)); }
break;
case 121:
//#line 153 "j0gram.y"
{
  yyval=j0.node("CondOrExpr", 1360, val_peek(2), val_peek(0)); }
break;
case 124:
//#line 157 "j0gram.y"
{
yyval=j0.node("Assignment",1370, val_peek(2), val_peek(1), val_peek(0)); }
break;
case 131:
//#line 162 "j0gram.y"
{
  yyval=j0.node("ArrayAccess", 1390, val_peek(3), val_peek(1));}
break;
//#line 955 "parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = j0.yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public parser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public parser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
