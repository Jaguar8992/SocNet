# Java PRO Social Network 14

Skillbox Java PRO study project about developing social network app.  


Programming Style Convention.
	
	Comments:
	All comments should be written in English.
	We should try to use C-style as in example below:
	Exmpl:
    /**
     * this is a new comment that explains some part of a code.
     */
	
	Naming:
	We should use a "camelStyle" to name methods and variables.
	Exmpl: 
		private void dropPosition()
		{
			Integer currentPosition = 0;
			//...some code
		}
	--------------
	We should use a "CamelStyle" with High first letter to name class.
	Exmpl: 
		class PositionSelector()
		{
			//...some code
		}
	--------------
	We should use a "snake_style" in database table names.
	Exmpl: 
	CREATE TABLE block_history;
	--------------
	
	We are allowed to use oneletter variable only in loops (for, while etc.).
	
	Another names of variables should be readable, clear and hould give a point of content.
	No Magic Numbers!
	Exmpl:
		int width = 640;
		int high = 480;
		
		private void getResolution()
		{
			int pointsCount = width * high;
			//...some code
		}
	--------------
	
	Lenghts:
	
	All private methods should have lenght of name as long as you wish but try do it not longer than 10 different words.
	Exmpl: 
		private void giveSelectedGearPositionInAutomaticGearSelector(){}
	--------------
	
	All public methods should be as short as visible they are. Try to call them no longer than 5 different words.
	Exmpl: 
		public Integer returnUserIndex()
		{
			//...some code
			return 0;
		}
	--------------
	
	A block is any code surrounded by curly braces "{" and "}", and first brace should be and the next line:
	Exmpl:
		
		private void exec()
		{
		
		}
	--------------
	
	Each line should contain at most one statement.
	Exmpl:
		
		private void exec()
		{
			increasedValue++;
			decreasedValue--;
			multiplication = one * two;
			multiplication = multiplication * 2;
			... etc
		}
	--------------
	
	Blank Spaces:
	All binary operators except . should be separated from their operands by spaces. 
	Blank spaces should never separate unary operators such as unary minus, increment ("++"), and decrement ("--") from their operands.
	
	Exmpl:
		a += c + d;
		a = (a + b) / (c * d);
	
		System.out.println("size is " + foo + "\n");
	
	The expressions in a for statement should be separated by blank spaces.
	Exmpl:
		for (int i = 0; i < 10; i++) {}
		
	Casts should be followed by a blank space.
	Exmpl:
		myMethod((byte) num, (Object) x);