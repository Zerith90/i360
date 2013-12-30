package CP_Classes;

import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.XComponentContext;
import com.sun.star.bridge.XUnoUrlResolver;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.lang.XComponent;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.frame.XStorable;
import com.sun.star.frame.XModel;
import com.sun.star.uno.XInterface;

import com.sun.star.chart.*;
import com.sun.star.sheet.*;
import com.sun.star.container.*;
import com.sun.star.table.*;
import com.sun.star.beans.*;
import com.sun.star.lang.*;
import com.sun.star.drawing.*;
import com.sun.star.text.XText;
import com.sun.star.text.XTextCursor;
import com.sun.star.text.XTextField;
import com.sun.star.text.XTextRange;
import com.sun.star.awt.*;
import com.sun.star.document.XEmbeddedObjectSupplier;

/*
 *CHANGE LOG
 *====================================================================================================================
 *	Date		Modified by			Method(s)							Reason
 *====================================================================================================================
 * 	11/07/2012	Albert				setBarTwoColor()					add method to draw charts with 2 different bar colors	
 *
 *  24/07/2012  Liu Taichen			storeAsPDF(XMultiComponentFactory,  To allow certain numbers of pages of an excel to be saved  to pdf
 *  								 XComponent, String, int)
 */

public class OpenOffice {

	private XComponentContext xRemoteContext = null;
	private XTableChart xtablechart = null; // Table chart, which type will be
											// changed.

	// Default fontsize for charts
	private float fontSize = 8;

	// Default font/char height for charts
	private double charHeight = 8;

	/**
	 * Open the connection in order to use Open Office.
	 * 
	 * @return the service manager.
	 */
	public XMultiComponentFactory getRemoteServiceManager(String unoUrl)
			throws java.lang.Exception {

		if (xRemoteContext == null) {

			// First step: create local component context, get local
			// servicemanager and

			// ask it to create a UnoUrlResolver object with an XUnoUrlResolver
			// interface

			XComponentContext xLocalContext =

			com.sun.star.comp.helper.Bootstrap
					.createInitialComponentContext(null);

			XMultiComponentFactory xLocalServiceManager = xLocalContext
					.getServiceManager();

			Object urlResolver = xLocalServiceManager
					.createInstanceWithContext(

					"com.sun.star.bridge.UnoUrlResolver", xLocalContext);

			// query XUnoUrlResolver interface from urlResolver object

			XUnoUrlResolver xUnoUrlResolver = (XUnoUrlResolver) UnoRuntime
					.queryInterface(

					XUnoUrlResolver.class, urlResolver);

			// Second step: use xUrlResolver interface to import the remote
			// StarOffice.ServiceManager,

			// retrieve its property DefaultContext and get the remote
			// servicemanager

			Object initialObject = xUnoUrlResolver.resolve(unoUrl);

			XPropertySet xPropertySet = (XPropertySet) UnoRuntime
					.queryInterface(

					XPropertySet.class, initialObject);

			Object context = xPropertySet.getPropertyValue("DefaultContext");

			xRemoteContext = (XComponentContext) UnoRuntime.queryInterface(

			XComponentContext.class, context);

		}

		return xRemoteContext.getServiceManager();

	}

	/**
	 * Once you get the service manager, open the existing excel document. This
	 * is if we are using template. It returns the document that has been
	 * loaded.
	 * 
	 * @param storedURL
	 *            the filename of the template to be loaded
	 * @return the opened document
	 */
	public XComponent openDoc(XMultiComponentFactory xRemoteServiceManager,
			String storedURL) throws java.lang.Exception {
		xRemoteServiceManager = this
				.getRemoteServiceManager("uno:socket,host=localhost,port=8100;urp;StarOffice.ServiceManager");

		// retrieve the Desktop object, we need its XComponentLoader
		Object desktop = xRemoteServiceManager.createInstanceWithContext(
				"com.sun.star.frame.Desktop", xRemoteContext);

		// query the XComponentLoader interface from the Desktop service
		XComponentLoader xComponentLoader = (XComponentLoader) UnoRuntime
				.queryInterface(XComponentLoader.class, desktop);

		// the boolean property Hidden tells the office to open a file in hidden
		// mode
		PropertyValue[] loadProps = new PropertyValue[1];
		loadProps[0] = new PropertyValue();
		loadProps[0].Name = "Hidden";
		loadProps[0].Value = new Boolean(true); // set the file to be hidden
												// upon generating

		// the directory where the template is stored.
		String loadUrl = storedURL;
		System.out.println("URL=" + loadUrl);
		// load the existing document
		XComponent xDoc = xComponentLoader.loadComponentFromURL(loadUrl,
				"_blank", 0, loadProps);

		return xDoc;
	}

	/**
	 * Store / save a document, using the MS Excel 97/2000/XP Filter. Passing in
	 * the opened document and address to be stored as the parameter
	 * 
	 * @param xRemoteServiceManager
	 *            the service manager returned when you get the service manager
	 * @param xDoc
	 *            document returned when you opened/loaded a spreadsheet
	 * @param storeUrl
	 *            the filename to be stored / saved as
	 */
	public void storeDocComponent(XMultiComponentFactory xRemoteServiceManager,
			XComponent xDoc, String storeUrl) throws java.lang.Exception {

		XStorable xStorable = (XStorable) UnoRuntime.queryInterface(
				XStorable.class, xDoc);

		PropertyValue[] storeProps = new PropertyValue[1];

		storeProps[0] = new PropertyValue();

		storeProps[0].Name = "FilterName";

		storeProps[0].Value = "MS Excel 97";
		System.out.println("storeURL" + storeUrl);
		// save as the template with new filename and location.
		xStorable.storeAsURL(storeUrl, storeProps);

	}

	/**
	 * Store / save a document, using the PDF Filter Passing in the opened
	 * document and address to be stored as the parameter
	 * 
	 * @param xRemoteServiceManager
	 *            the service manager returned when you get the service manager
	 * @param xDoc
	 *            document returned when you opened/loaded a spreadsheet
	 * @param storeUrl
	 *            the filename to be stored / saved as
	 * @param pageCount
	 *            number of pages to be printed
	 * @Author Liu Taichen
	 */
	public void storeAsPDF(XMultiComponentFactory xRemoteServiceManager,
			XComponent xDoc, String storeUrl, int pageCount)
			throws java.lang.Exception {

		try {
			XStorable xStorable = (XStorable) UnoRuntime.queryInterface(
					XStorable.class, xDoc);
			int name = storeUrl.indexOf(".");
			storeUrl = storeUrl.substring(0, name + 1) + "pdf";
			PropertyValue[] storeProps = new PropertyValue[2];
			PropertyValue[] FilterProps = new PropertyValue[2];
			storeProps[0] = new PropertyValue();

			storeProps[0].Name = "FilterName";

			storeProps[0].Value = "calc_pdf_Export";

			FilterProps[0] = new PropertyValue();
			FilterProps[0].Name = "PageRange";
			FilterProps[0].Value = "1-" + pageCount;

			storeProps[1] = new PropertyValue();
			storeProps[1].Name = "FilterData";
			storeProps[1].Value = FilterProps;
			// save as the template with new filename and location.

			xStorable.storeToURL(storeUrl, storeProps);
		} catch (Exception e) {
			System.out.println(e + " storeDocumentAsPDF");
		}

	}

	/**
	 * Store / save a document, using the PDF Filter Passing in the opened
	 * document and address to be stored as the parameter
	 * 
	 * @param xRemoteServiceManager
	 *            the service manager returned when you get the service manager
	 * @param xDoc
	 *            document returned when you opened/loaded a spreadsheet
	 * @param storeUrl
	 *            the filename to be stored / saved as
	 * @Author Liu Taichen
	 */
	public void storeDocComponentAsPDF(
			XMultiComponentFactory xRemoteServiceManager, XComponent xDoc,
			String storeUrl) throws java.lang.Exception {

		try {
			XStorable xStorable = (XStorable) UnoRuntime.queryInterface(
					XStorable.class, xDoc);
			int name = storeUrl.indexOf(".");
			storeUrl = storeUrl.substring(0, name + 1) + "pdf";
			PropertyValue[] storeProps = new PropertyValue[2];

			storeProps[0] = new PropertyValue();

			storeProps[0].Name = "FilterName";

			storeProps[0].Value = "calc_pdf_Export";

			// save as the template with new filename and location.

			xStorable.storeToURL(storeUrl, storeProps);
		} catch (Exception e) {
			System.out.println(e + " storeDocumentAsPDF");
		}

	}

	/**
	 * Retrieve the specific sheet (ex. Sheet1, Sheet2, Sheet3, etc).
	 * 
	 * @param xSpreadsheetComponent
	 *            component returned when you called the openDoc
	 * @param sheetName
	 *            which particular sheet to be retrieved (Sheet1, Sheet2, etc)
	 * @return the sheet retrieved
	 */
	public XSpreadsheet getSheet(XComponent xSpreadsheetComponent,
			String sheetName) throws java.lang.Exception {

		// query its XSpreadsheetDocument interface, we want to use getSheets()
		XSpreadsheetDocument xSpreadsheetDocument = (XSpreadsheetDocument) UnoRuntime
				.queryInterface(XSpreadsheetDocument.class,
						xSpreadsheetComponent);

		// use getSheets to get spreadsheets container
		XSpreadsheets xSpreadsheets = xSpreadsheetDocument.getSheets();

		// retrieve the sheet based on sheet name, it can also be retrieved by
		// index 0, 1, 2, etc. But here I implemeted using the sheet name.
		Object sheet = xSpreadsheets.getByName(sheetName);
		XSpreadsheet xSpreadsheet = (XSpreadsheet) UnoRuntime.queryInterface(
				XSpreadsheet.class, sheet);

		return xSpreadsheet;
	}

	/**
	 * Find and replace specific strings.
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet returned when you called getSheet function
	 * @param find
	 *            the string to be searched/found
	 * @param replace
	 *            the string which used to replace
	 */
	public void findAndReplace(XSpreadsheet xSpreadsheet, String find,
			String replace) throws java.lang.Exception {

		// --- Replace text in all cells from A1 to I1000. ---
		// We must specify the range, so that the API knows where to look for
		// the string.
		// It will only look within the range that is specified.
		XCellRange xCellRange = xSpreadsheet.getCellRangeByName("A1:AI6000");
		com.sun.star.util.XReplaceable xReplace = (com.sun.star.util.XReplaceable) UnoRuntime
				.queryInterface(com.sun.star.util.XReplaceable.class,
						xCellRange);
		com.sun.star.util.XReplaceDescriptor xReplaceDesc = xReplace
				.createReplaceDescriptor();

		xReplaceDesc.setSearchString(find);
		xReplaceDesc.setReplaceString(replace);

		// property SearchWords searches for whole cells!
		xReplaceDesc.setPropertyValue("SearchWords", new Boolean(false));

		// the replaceAll functions always return total words replaced, but we
		// are not using this information, therefore we don't need to return the
		// count.
		int nCount = xReplace.replaceAll(xReplaceDesc);
	}

	/**
	 * Find the particular string and get the location of the cell where the
	 * string is located.
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet returned when you called getSheet
	 * @param find
	 *            the string to be searched/found
	 * @return row and column address of the cell where the string is located
	 */
	public int[] findString(XSpreadsheet xSpreadsheet, String find)
			throws java.lang.Exception {

		// --- Search string in all cells from A1 to I6000. ---
		// We must specify the range, so that the API knows where to look for
		// the string.
		// It will only look within the range that is specified.
		int[] address = new int[2];
		;
		try {
			XCellRange xCellRange = xSpreadsheet
					.getCellRangeByName("A1:AI6000");
			com.sun.star.util.XSearchable xSearch = (com.sun.star.util.XSearchable) UnoRuntime
					.queryInterface(com.sun.star.util.XSearchable.class,
							xCellRange);
			com.sun.star.util.XSearchDescriptor xSearchDesc = xSearch
					.createSearchDescriptor();

			XInterface xSearchInterface = null;

			xSearchDesc.setSearchString(find);
			xSearchInterface = (XInterface) xSearch.findFirst(xSearchDesc);

			com.sun.star.sheet.XCellAddressable xRow = (com.sun.star.sheet.XCellAddressable) UnoRuntime
					.queryInterface(com.sun.star.sheet.XCellAddressable.class,
							xSearchInterface);

			address[0] = xRow.getCellAddress().Column;
			address[1] = xRow.getCellAddress().Row;
		} catch (Exception e) {
			System.out.println();
		}

		return address;
	}

	/**
	 * This function is to replace all the logo tags in the report with an
	 * image. The function will replace the main logo first, then replace the
	 * rest.
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet returned when you called getSheet
	 * @param xDoc
	 *            component returned when you called the openDoc
	 * @param logoTag
	 *            the tag used in the report template to determine the logo
	 * @param logoName
	 *            the filename of the logo file, must include the extension
	 */
	public void replaceLogo(XSpreadsheet xSpreadsheet, XComponent xDoc,
			String logoTag, String logoName) throws java.lang.Exception {

		// --- Search String in all cells from A1 to I1000. ---
		// We must specify the range, so that the API knows where to look for
		// the string.
		// It will only look within the range that is specified.
		XCellRange xCellRange = xSpreadsheet.getCellRangeByName("A1:I1000");
		com.sun.star.util.XSearchable xSearch = (com.sun.star.util.XSearchable) UnoRuntime
				.queryInterface(com.sun.star.util.XSearchable.class, xCellRange);
		com.sun.star.util.XSearchDescriptor xSearchDesc = xSearch
				.createSearchDescriptor();

		XTextRange xFound = null;
		XInterface xSearchInterface = null;

		// search for main logo
		xSearchDesc.setSearchString("<Main Logo>");
		xSearchInterface = (XInterface) xSearch.findFirst(xSearchDesc);

		if (xSearchInterface != null) {
			com.sun.star.sheet.XCellAddressable xRow = (com.sun.star.sheet.XCellAddressable) UnoRuntime
					.queryInterface(com.sun.star.sheet.XCellAddressable.class,
							xSearchInterface);

			int address[] = new int[2];
			address[0] = xRow.getCellAddress().Column;
			address[1] = xRow.getCellAddress().Row;

			// replace the main logo with blank characters, because if not, the
			// main logo tag will still be there and it can be seen by users.
			findAndReplace(xSpreadsheet, "<Main Logo>", "");

			// insert the image into the particular row column address
			// I did this separately from the rest images because the main logo
			// has bigger width and height.
			insertImage(xSpreadsheet, xDoc, logoName, 6000, 4000, address[1],
					address[0]);
		}

		// search for the rest of the logo tags.
		xSearchDesc.setSearchString(logoTag);
		xSearchInterface = (XInterface) xSearch.findFirst(xSearchDesc);

		if (xSearchInterface != null) {
			com.sun.star.sheet.XCellAddressable xRow = (com.sun.star.sheet.XCellAddressable) UnoRuntime
					.queryInterface(com.sun.star.sheet.XCellAddressable.class,
							xSearchInterface);

			int address[] = new int[2];
			address[0] = xRow.getCellAddress().Column;
			address[1] = xRow.getCellAddress().Row;

			insertImage(xSpreadsheet, xDoc, logoName, 4000, 1500, address[1],
					address[0]);

			xSearchInterface = (XInterface) xSearch.findNext(xSearchInterface,
					xSearchDesc);
			while (xSearchInterface != null) {
				xRow = (com.sun.star.sheet.XCellAddressable) UnoRuntime
						.queryInterface(
								com.sun.star.sheet.XCellAddressable.class,
								xSearchInterface);

				address[0] = xRow.getCellAddress().Column;
				address[1] = xRow.getCellAddress().Row;

				insertImage(xSpreadsheet, xDoc, logoName, 4000, 1500,
						address[1], address[0]);
				xSearchInterface = (XInterface) xSearch.findNext(
						xSearchInterface, xSearchDesc);
			}

			findAndReplace(xSpreadsheet, logoTag, "");
		}
	}

	/*
	 * Retrieve content from a cell.
	 * 
	 * @param xWorkSheet Name of the worksheet
	 * 
	 * @param row the specific row where the cell is
	 * 
	 * @param column the specific column where the cell is
	 * 
	 * @return result store the contents of the cell to pass back
	 * 
	 * @author: Mark Oei
	 * 
	 * @since v.1.3.12.68 (25 Mar 2010)
	 */
	public double getCellValue(XSpreadsheet xWorkSheet, int row, int column)
			throws java.lang.Exception {
		double result = 0.0;
		XCell xCell = xWorkSheet.getCellByPosition(column, row);
		result = xCell.getValue();
		return result;
	} // End of getCellValue()

	/**
	 * Insert String into a cell. This function can only be used to insert
	 * "String". If you insert numeric value using this function, it will be
	 * displayed with a single quotation mark in front.
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet returned when you called getSheet function
	 * @param input
	 *            the string to be written to the cell
	 * @param row
	 *            the specific row to input the string
	 * @param column
	 *            the specific column to input the string
	 */
	public void insertString(XSpreadsheet xSpreadsheet, String input, int row,
			int column) throws java.lang.Exception {
		// insert string
		XCell xCell = xSpreadsheet.getCellByPosition(column, row);
		com.sun.star.text.XText xCellText = (com.sun.star.text.XText) UnoRuntime
				.queryInterface(com.sun.star.text.XText.class, xCell);
		com.sun.star.text.XTextCursor xTextCursor = xCellText
				.createTextCursor();

		xCellText.insertString(xTextCursor, input, false);

		/*
		 * //no more needed because OO taken care of the \n //split the string
		 * if have escape character so that it can be displayed in different
		 * lines. //String temp1 [] = input.split("\n"); for(int i=0;
		 * i<temp1.length; i++) { xCellText.insertString(xTextCursor, temp1[i],
		 * false);
		 * 
		 * if(i < temp1.length-1) // we can use this to separate \n into few
		 * lines xCellText.insertControlCharacter(xTextCursor,
		 * com.sun.star.text.ControlCharacter.PARAGRAPH_BREAK, false); }
		 */
	}

	/**
	 * Insert numerical value into a cell. This function can only be used to
	 * insert numeric, it can be integer, float, or double.
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet returned when you called getSheet function
	 * @param input
	 *            the input to be written to the cell
	 * @param row
	 *            the specific row to input the number
	 * @param column
	 *            the specific column to input the number
	 */
	public void insertNumeric(XSpreadsheet xSpreadsheet, double input, int row,
			int column) throws java.lang.Exception {
		// insert string
		XCell xCell = xSpreadsheet.getCellByPosition(column, row);
		// insert double
		xCell.setValue(input);

	}

	/**
	 * Insert manual page break.
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet returned when you called getSheet function
	 * @param startCol
	 *            the column where the page break starts
	 * @param endCol
	 *            the column where the page break ends
	 * @param row
	 *            the row where the page break to be inserted
	 */
	public void insertPageBreak(XSpreadsheet xSpreadsheet, int startCol,
			int endCol, int row) throws java.lang.Exception {
		XCellRange xCellRange = xSpreadsheet.getCellRangeByPosition(startCol,
				row, endCol, row);

		com.sun.star.table.XColumnRowRange xColRowRange = (com.sun.star.table.XColumnRowRange) UnoRuntime
				.queryInterface(com.sun.star.table.XColumnRowRange.class,
						xCellRange);

		// insert row
		com.sun.star.table.XTableRows xRows = xColRowRange.getRows();
		Object aRowObj = xRows.getByIndex(0);

		// insert page break
		com.sun.star.beans.XPropertySet xPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime
				.queryInterface(com.sun.star.beans.XPropertySet.class, aRowObj);

		xPropSet.setPropertyValue("IsStartOfNewPage", new Boolean(true));

	}

	/**
	 * Merge Cells
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet returned when you called getSheet function
	 * @param startCol
	 *            the column where the merging starts
	 * @param endCol
	 *            the column where the merging ends
	 * @param startRow
	 *            the row where the merging starts
	 * @param endRow
	 *            the row where the merging ends
	 */
	public void mergeCells(XSpreadsheet xSpreadsheet, int startCol, int endCol,
			int startRow, int endRow) throws java.lang.Exception {
		XCellRange xCellRange = xSpreadsheet.getCellRangeByPosition(startCol,
				startRow, endCol, endRow);

		// --- Merge cells. ---
		com.sun.star.util.XMergeable xMerge = (com.sun.star.util.XMergeable) UnoRuntime
				.queryInterface(com.sun.star.util.XMergeable.class, xCellRange);
		xMerge.merge(true);

		// look at Service CellProperties
		com.sun.star.beans.XPropertySet xPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime
				.queryInterface(com.sun.star.beans.XPropertySet.class,
						xCellRange);
		xPropSet.setPropertyValue("IsTextWrapped", new Boolean(true));
	}

	/**
	 * Merge Cells
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet returned when you called getSheet function
	 * @param startCol
	 *            the column where the merging starts
	 * @param endCol
	 *            the column where the merging ends
	 * @param startRow
	 *            the row where the merging starts
	 * @param endRow
	 *            the row where the merging ends
	 */
	public void unMergeCells(XSpreadsheet xSpreadsheet, int startCol,
			int endCol, int startRow, int endRow) throws java.lang.Exception {
		XCellRange xCellRange = xSpreadsheet.getCellRangeByPosition(startCol,
				startRow, endCol, endRow);

		// --- Merge cells. ---
		com.sun.star.util.XMergeable xMerge = (com.sun.star.util.XMergeable) UnoRuntime
				.queryInterface(com.sun.star.util.XMergeable.class, xCellRange);
		xMerge.merge(false);

		// look at Service CellProperties
		com.sun.star.beans.XPropertySet xPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime
				.queryInterface(com.sun.star.beans.XPropertySet.class,
						xCellRange);
		xPropSet.setPropertyValue("IsTextWrapped", new Boolean(false));
	}

	/**
	 * Insert rows. OO allows insertion of multiple rows at the same time,
	 * therefore you need to provide startCol, endCol, startRow, endRow to
	 * specify the CellRange where the insertion is done.
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet returned when you called getSheet function
	 * @param startCol
	 *            the column where the insertion starts
	 * @param endCol
	 *            the column where the insertion ends
	 * @param startRow
	 *            the row where the insertion starts
	 * @param endRow
	 *            the row where the insertion ends
	 * @param totalRows
	 *            total rows to be inserted
	 * @param row
	 *            the row where the insertion is done (normally is first row
	 *            from the range) Ex: If you specify the range of A6 to A10,
	 *            this means that the insertion can only be done within this
	 *            range. From the range A6-A10, there are 5 rows. So, you can
	 *            only insert maximum 5 rows, totalRows <= 5. If row = 1, this
	 *            means that the insertion starts at A6, row=2, starts at A7.
	 */
	public void insertRows(XSpreadsheet xSpreadsheet, int startCol, int endCol,
			int startRow, int endRow, int totalRows, int row)
			throws java.lang.Exception {
		XCellRange xCellRange = xSpreadsheet.getCellRangeByPosition(startCol,
				startRow, endCol, endRow);

		com.sun.star.table.XColumnRowRange xColRowRange = (com.sun.star.table.XColumnRowRange) UnoRuntime
				.queryInterface(com.sun.star.table.XColumnRowRange.class,
						xCellRange);

		// insert row
		com.sun.star.table.XTableRows xRows = xColRowRange.getRows();

		xRows.insertByIndex(row, totalRows); // row, count
	}

	/**
	 * Set Cell Background Color Colors are given in ARGB, comprised of four
	 * bytes for alpha-red-green-blue as in 0xAARRGGBB.
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet returned when you called getSheet function
	 * @param startCol
	 *            the column where the range starts
	 * @param endCol
	 *            the column where the range ends
	 * @param startRow
	 *            the row where the range starts
	 * @param endRow
	 *            the row where the range ends
	 * @color the specific color, default cell background is GREY, which is
	 *        represented as 12632256.
	 */
	public void setBGColor(XSpreadsheet xSpreadsheet, int startCol, int endCol,
			int startRow, int endRow, int color) throws java.lang.Exception {
		XCellRange xCellRange = xSpreadsheet.getCellRangeByPosition(startCol,
				startRow, endCol, endRow);

		// get the properties of the cell range
		com.sun.star.beans.XPropertySet xPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime
				.queryInterface(com.sun.star.beans.XPropertySet.class,
						xCellRange);

		// set the back color of the cell range, refer to CellProperties Service
		xPropSet.setPropertyValue("CellBackColor", new Integer(color));
	}

	/**
	 * Set Cell Allignment, apply for horizontal and vertical.
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet returned when you called getSheet function
	 * @param startCol
	 *            the column where the range starts
	 * @param endCol
	 *            the column where the range ends
	 * @param startRow
	 *            the row where the range starts
	 * @param endRow
	 *            the row where the range ends
	 * 
	 * @param type
	 *            1 = Horizontal 2 = Vertical
	 * 
	 * @param allignment
	 *            1 = LEFT 2 = CENTER 3 = RIGHT 4 = STANDARD
	 * 
	 *            1 = TOP 2 = CENTER 3 = BOTTOM 4 = STANDARD
	 * 
	 */
	public void setCellAllignment(XSpreadsheet xSpreadsheet, int startCol,
			int endCol, int startRow, int endRow, int type, int allignment)
			throws java.lang.Exception {
		XCellRange xCellRange = xSpreadsheet.getCellRangeByPosition(startCol,
				startRow, endCol, endRow);

		// get the properties of the cell range
		com.sun.star.beans.XPropertySet xPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime
				.queryInterface(com.sun.star.beans.XPropertySet.class,
						xCellRange);

		if (type == 1) {
			switch (allignment) {
			case 1:
				xPropSet.setPropertyValue("HoriJustify",
						com.sun.star.table.CellHoriJustify.LEFT);
				break;
			case 2:
				xPropSet.setPropertyValue("HoriJustify",
						com.sun.star.table.CellHoriJustify.CENTER);
				break;
			case 3:
				xPropSet.setPropertyValue("HoriJustify",
						com.sun.star.table.CellHoriJustify.RIGHT);
				break;
			case 4:
				xPropSet.setPropertyValue("HoriJustify",
						com.sun.star.table.CellHoriJustify.STANDARD);
				break;
			}
		} else if (type == 2) {
			switch (allignment) {
			case 1:
				xPropSet.setPropertyValue("VertJustify",
						com.sun.star.table.CellVertJustify.TOP);
				break;
			case 2:
				xPropSet.setPropertyValue("VertJustify",
						com.sun.star.table.CellVertJustify.CENTER);
				break;
			case 3:
				xPropSet.setPropertyValue("VertJustify",
						com.sun.star.table.CellVertJustify.BOTTOM);
				break;
			case 4:
				xPropSet.setPropertyValue("VertJustify",
						com.sun.star.table.CellVertJustify.STANDARD);
				break;
			}
		}
	}

	/**
	 * Added by Alvis on 01-Sep-09 for text alignment
	 * 
	 * Decrease left indentation of text cell.
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet returned when you called getSheet function
	 * @param startCol
	 *            the column where the range starts
	 * @param endCol
	 *            the column where the range ends
	 * @param startRow
	 *            the row where the range starts
	 * @param endRow
	 *            the row where the range ends
	 */
	public void decreaseIndent(XSpreadsheet xSpreadsheet, int startCol,
			int endCol, int startRow, int endRow) throws java.lang.Exception {
		XCellRange xCellRange = xSpreadsheet.getCellRangeByPosition(startCol,
				startRow, endCol, endRow);

		// --- Change indentation. ---
		com.sun.star.util.XIndent xIndent = (com.sun.star.util.XIndent) UnoRuntime
				.queryInterface(com.sun.star.util.XIndent.class, xCellRange);
		xIndent.decrementIndent();

	}

	/**
	 * Font Settings, set the font to be BOLD.
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet returned when you called getSheet function
	 * @param startCol
	 *            the column where the range starts
	 * @param endCol
	 *            the column where the range ends
	 * @param startRow
	 *            the row where the range starts
	 * @param endRow
	 *            the row where the range ends
	 */
	public void setFontBold(XSpreadsheet xSpreadsheet, int startCol,
			int endCol, int startRow, int endRow) throws java.lang.Exception {
		XCellRange xCellRange = xSpreadsheet.getCellRangeByPosition(startCol,
				startRow, endCol, endRow);

		// look at Service CharacterProperties
		com.sun.star.beans.XPropertySet xPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime
				.queryInterface(com.sun.star.beans.XPropertySet.class,
						xCellRange);

		// set the font color of the cell range
		xPropSet.setPropertyValue("CharWeight", new Float(
				com.sun.star.awt.FontWeight.BOLD));
	}

	/**
	 * Font Settings, set the font to be Normal. For cases where generated
	 * report has random bold letters
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet returned when you called getSheet function
	 * @param startCol
	 *            the column where the range starts
	 * @param endCol
	 *            the column where the range ends
	 * @param startRow
	 *            the row where the range starts
	 * @param endRow
	 *            the row where the range ends
	 */
	public void setFontNormal(XSpreadsheet xSpreadsheet, int startCol,
			int endCol, int startRow, int endRow) throws java.lang.Exception {
		XCellRange xCellRange = xSpreadsheet.getCellRangeByPosition(startCol,
				startRow, endCol, endRow);

		// look at Service CharacterProperties
		com.sun.star.beans.XPropertySet xPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime
				.queryInterface(com.sun.star.beans.XPropertySet.class,
						xCellRange);

		// set the font color of the cell range
		xPropSet.setPropertyValue("CharWeight", new Float(
				com.sun.star.awt.FontWeight.NORMAL));
	}

	/**
	 * Font Settings, set the font size.
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet returned when you called getSheet function
	 * @param startCol
	 *            the column where the range starts
	 * @param endCol
	 *            the column where the range ends
	 * @param startRow
	 *            the row where the range starts
	 * @param endRow
	 *            the row where the range ends
	 * @param fontSize
	 *            font size of the alphabets
	 */
	public void setFontSize(XSpreadsheet xSpreadsheet, int startCol,
			int endCol, int startRow, int endRow, float fontSize)
			throws java.lang.Exception {
		XCellRange xCellRange = xSpreadsheet.getCellRangeByPosition(startCol,
				startRow, endCol, endRow);

		// look at Service CharacterProperties
		com.sun.star.beans.XPropertySet xPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime
				.queryInterface(com.sun.star.beans.XPropertySet.class,
						xCellRange);

		xPropSet.setPropertyValue("CharHeight", new Float(fontSize));
	}

	/**
	 * Font Settings, set the font to be ITALIC.
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet returned when you called getSheet function
	 * @param startCol
	 *            the column where the range starts
	 * @param endCol
	 *            the column where the range ends
	 * @param startRow
	 *            the row where the range starts
	 * @param endRow
	 *            the row where the range ends
	 */
	public void setFontItalic(XSpreadsheet xSpreadsheet, int startCol,
			int endCol, int startRow, int endRow) throws java.lang.Exception {
		XCellRange xCellRange = xSpreadsheet.getCellRangeByPosition(startCol,
				startRow, endCol, endRow);

		// look at Service CharacterProperties
		com.sun.star.beans.XPropertySet xPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime
				.queryInterface(com.sun.star.beans.XPropertySet.class,
						xCellRange);

		// set the font color of the cell range
		xPropSet.setPropertyValue("CharPosture",
				com.sun.star.awt.FontSlant.ITALIC);
	}

	/**
	 * Font Settings, set the font to be NONE ITALIC. For cases where generated
	 * report has random bold letters
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet returned when you called getSheet function
	 * @param startCol
	 *            the column where the range starts
	 * @param endCol
	 *            the column where the range ends
	 * @param startRow
	 *            the row where the range starts
	 * @param endRow
	 *            the row where the range ends
	 */
	public void setFontRemoveItalic(XSpreadsheet xSpreadsheet, int startCol,
			int endCol, int startRow, int endRow) throws java.lang.Exception {
		XCellRange xCellRange = xSpreadsheet.getCellRangeByPosition(startCol,
				startRow, endCol, endRow);

		// look at Service CharacterProperties
		com.sun.star.beans.XPropertySet xPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime
				.queryInterface(com.sun.star.beans.XPropertySet.class,
						xCellRange);

		// set the font color of the cell range
		xPropSet.setPropertyValue("CharPosture",
				com.sun.star.awt.FontSlant.NONE);
	}

	/**
	 * Font Settings, set the font to be UNDERLINE.
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet returned when you called getSheet function
	 * @param startCol
	 *            the column where the range starts
	 * @param endCol
	 *            the column where the range ends
	 * @param startRow
	 *            the row where the range starts
	 * @param endRow
	 *            the row where the range ends
	 */
	public void setFontUnderline(XSpreadsheet xSpreadsheet, int startCol,
			int endCol, int startRow, int endRow) throws java.lang.Exception {
		XCellRange xCellRange = xSpreadsheet.getCellRangeByPosition(startCol,
				startRow, endCol, endRow);

		// look at Service CharacterProperties
		com.sun.star.beans.XPropertySet xPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime
				.queryInterface(com.sun.star.beans.XPropertySet.class,
						xCellRange);

		// set the font color of the cell range
		xPropSet.setPropertyValue("CharUnderline", new Boolean(true));
	}

	/**
	 * Add Border to a cell, table, or range.
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet returned when you called getSheet function
	 * @param startCol
	 *            the column where the range starts
	 * @param endCol
	 *            the column where the range ends
	 * @param startRow
	 *            the row where the range starts
	 * @param endRow
	 *            the row where the range ends
	 * @param vertical
	 *            defines whether to include the vertical line in the center of
	 *            the range
	 * @param horizontal
	 *            defines whether to include the horizontal line in the center
	 *            of the range
	 * @param left
	 *            defines the left border
	 * @param right
	 *            defines the right border
	 * @param top
	 *            defines the top border
	 * @param bottom
	 *            defines the bottom border
	 */
	public void setTableBorder(XSpreadsheet xSpreadsheet, int startCol,
			int endCol, int startRow, int endRow, boolean vertical,
			boolean horizontal, boolean left, boolean right, boolean top,
			boolean bottom) throws java.lang.Exception {
		XCellRange xCellRange = xSpreadsheet.getCellRangeByPosition(startCol,
				startRow, endCol, endRow);

		// look at Service CharacterProperties
		com.sun.star.beans.XPropertySet xPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime
				.queryInterface(com.sun.star.beans.XPropertySet.class,
						xCellRange);

		BorderLine theLine = new BorderLine();
		// apply line description to all border lines and make them valid
		TableBorder bord = new TableBorder();
		theLine.Color = 0;
		theLine.OuterLineWidth = 10;

		bord.VerticalLine = bord.HorizontalLine = bord.LeftLine = bord.RightLine = bord.TopLine = bord.BottomLine = theLine;
		bord.IsVerticalLineValid = vertical;
		bord.IsHorizontalLineValid = horizontal;
		bord.IsLeftLineValid = left;
		bord.IsRightLineValid = right;
		bord.IsTopLineValid = top;
		bord.IsBottomLineValid = bottom;

		// bord.IsVerticalLineValid = bord.IsHorizontalLineValid =
		// bord.IsLeftLineValid = bord.IsRightLineValid =
		// bord.IsTopLineValid = bord.IsBottomLineValid = true;

		xPropSet.setPropertyValue("TableBorder", bord);
	}

	/************************************ CHART *********************************************************/

	/**
	 * Creating an empty OpenOffice.org Calc document, inserting data, and
	 * getting a chart by the chart name. You need to specify the chart name so
	 * that it knows which chart you are referring to.
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet where the chart will be displayed
	 * @param xSpreadsheet2
	 *            the specific sheet returned where you write all the chart data
	 *            (data range)
	 * @param int startCol the column where the range starts
	 * @param int endCol the column where the range ends
	 * @param int startRow the row where the range starts
	 * @param int endRow the row where the range ends
	 * @param String
	 *            name the chart name, it can be any name, but it has to be
	 *            unique amongst all the charts in that document
	 * @param int width the widht of the chart
	 * @param int height the height of the chart
	 * @param int chartStartRow determines the specific row where the chart will
	 *        be displayed
	 * @param int chartStartColumn determines the specific col where the chart
	 *        will be displayed
	 * 
	 * @return the tableChart created
	 */
	public XTableChart getChart(XSpreadsheet xSpreadsheet,
			XSpreadsheet xSpreadsheet2, int startCol, int endCol, int startRow,
			int endRow, String name, int width, int height, int chartStartRow,
			int chartStartColumn) {
		try {

			XTableChart xtablechart = null;

			// to set the chart into specific position
			XPropertySet xPropSet = null;

			// Get the cell by defining the row and column
			XCell xCell = xSpreadsheet.getCellByPosition(chartStartColumn,
					chartStartRow);// column, row

			// get the properties of the cell
			xPropSet = (XPropertySet) UnoRuntime.queryInterface(
					XPropertySet.class, xCell);

			// Get the XY Coordinates based on row and column, so that we can
			// integrate into the Graphics position
			Point position = (com.sun.star.awt.Point) xPropSet
					.getPropertyValue("Position");

			// Create a rectangle, which holds the size of the chart.
			Rectangle rectangle = new Rectangle();
			rectangle.X = position.X;
			rectangle.Y = position.Y;
			rectangle.Width = width;
			rectangle.Height = height;

			// Get the cell range of the written values.
			XCellRange xcellrangeChart = xSpreadsheet2.getCellRangeByPosition(
					startCol, startRow, endCol, endRow);

			// Get the addressable cell range.
			XCellRangeAddressable xcellrangeaddressable = (XCellRangeAddressable) UnoRuntime
					.queryInterface(XCellRangeAddressable.class,
							xcellrangeChart);

			// Get the cell range address.
			CellRangeAddress cellrangeaddress = xcellrangeaddressable
					.getRangeAddress();

			// Create the cell range address for the chart.
			CellRangeAddress[] cellrangeaddressChart = new CellRangeAddress[1];
			cellrangeaddressChart[0] = cellrangeaddress;

			// Get the table charts supplier of the spreadsheet.
			XTableChartsSupplier xtablechartssupplier = (XTableChartsSupplier) UnoRuntime
					.queryInterface(XTableChartsSupplier.class, xSpreadsheet);

			// Get all table charts of the spreadsheet.
			XTableCharts xtablecharts = xtablechartssupplier.getCharts();
			// Create a table chart with all written values.
			xtablecharts.addNewByName(name, rectangle, cellrangeaddressChart,
					true, true);

			// Get the created table chart.
			xtablechart = (XTableChart) UnoRuntime.queryInterface(
					XTableChart.class, ((XNameAccess) UnoRuntime
							.queryInterface(XNameAccess.class, xtablecharts))
							.getByName(name));

			return xtablechart;
		} catch (Exception exception) {
			System.err.println(exception);

		}

		return null;
	}

	public void deleteChart(XSpreadsheet xSpreadsheet, String name) {
		// Get the table charts supplier of the spreadsheet
		XTableChartsSupplier xtablechartssupplier = (XTableChartsSupplier) UnoRuntime
				.queryInterface(XTableChartsSupplier.class, xSpreadsheet);

		// Get all table charts of the spreadsheet
		XTableCharts xtablecharts = xtablechartssupplier.getCharts();
		// Delete table
		xtablecharts.removeByName(name);

		/*
		 * String [] temp = xtablecharts.getElementNames(); for(int i=0;
		 * i<temp.length; i++) System.out.println(temp[i]);
		 * xtablecharts.removeByName(temp[temp.length-1]);
		 */
	}

	/**
	 * This method will change the type of a specified chart.
	 * 
	 * @param stringType
	 *            The chart will be converted to this type.
	 * @param xtablechart
	 *            the table chart returned when you create a new chart
	 * 
	 *            Ex of stringType/chart type: Radar Graph
	 *            com.sun.star.chart.NetDiagram Bar Chart
	 *            com.sun.star.chart.BarDiagram Line Diagram
	 *            com.sun.star.chart.LineDiagram
	 * 
	 *            To get more chart type, please refer to the API under
	 *            com.sun.star.chart.*;
	 */
	public void changeChartType(String stringType, XTableChart xtablechart)
			throws Exception {
		XEmbeddedObjectSupplier xembeddedobjectsupplier = (XEmbeddedObjectSupplier) UnoRuntime
				.queryInterface(XEmbeddedObjectSupplier.class, xtablechart);
		XInterface xinterface = xembeddedobjectsupplier.getEmbeddedObject();

		XChartDocument xchartdocument = (XChartDocument) UnoRuntime
				.queryInterface(XChartDocument.class, xinterface);
		XDiagram xdiagram = (XDiagram) xchartdocument.getDiagram();

		XMultiServiceFactory xmultiservicefactory = (XMultiServiceFactory) UnoRuntime
				.queryInterface(XMultiServiceFactory.class, xchartdocument);
		Object object = xmultiservicefactory.createInstance(stringType);
		xdiagram = (XDiagram) UnoRuntime.queryInterface(XDiagram.class, object);

		xchartdocument.setDiagram(xdiagram);

		/**
		 * Added the width of Line Diagram and NetDiagram. The default line is
		 * too thin, very hard to see. Refer to com :: sun :: star :: drawing ::
		 * LineProperties (Service)
		 */
		if (stringType.equals("com.sun.star.chart.NetDiagram")
				|| stringType.equals("com.sun.star.chart.LineDiagram")) {
			XPropertySet xPropSet = (XPropertySet) UnoRuntime.queryInterface(
					XPropertySet.class, object);
			xPropSet.setPropertyValue("LineWidth", new Integer(50));
		}

		/**
		 * Add the symbol for each data value, otherwise, it is very difficult
		 * to be seen.
		 */
		if (stringType.equals("com.sun.star.chart.LineDiagram")) {
			XPropertySet xPropSet = (XPropertySet) UnoRuntime.queryInterface(
					XPropertySet.class, object);
			xPropSet.setPropertyValue("SymbolType", new Integer(
					com.sun.star.chart.ChartSymbolType.SYMBOL1));
		}
	}

	/**
	 * Not working yet
	 * 
	 * @param iMaxIndex
	 * @throws Exception
	 * @author Maruli
	 */
	public void changeChartLineColours(int iMaxIndex) throws Exception {
		XEmbeddedObjectSupplier xembeddedobjectsupplier = (XEmbeddedObjectSupplier) UnoRuntime
				.queryInterface(XEmbeddedObjectSupplier.class, xtablechart);
		XInterface xinterface = xembeddedobjectsupplier.getEmbeddedObject();

		XChartDocument xchartdocument = (XChartDocument) UnoRuntime
				.queryInterface(XChartDocument.class, xinterface);
		XDiagram xdiagram = (XDiagram) xchartdocument.getDiagram();

		xchartdocument.setDiagram(xdiagram);

		XPropertySet xPropSet = xdiagram.getDataPointProperties(iMaxIndex, 1);
		xPropSet.setPropertyValue("CharColor", new Integer(0x993366));
	}

	/**
	 * Set the Chart Main Title. This function also set all the font properties
	 * to be the default size.
	 * 
	 * @param xtablechart
	 *            the xtablechart returned when you create a new chart
	 * 
	 * @return xtablechart tablechart modified
	 */
	public XTableChart setChartTitle(XTableChart xtablechart, String title)
			throws Exception {

		XEmbeddedObjectSupplier xembeddedobjectsupplier = (XEmbeddedObjectSupplier) UnoRuntime
				.queryInterface(XEmbeddedObjectSupplier.class, xtablechart);
		XInterface xinterface = xembeddedobjectsupplier.getEmbeddedObject();

		// to set the chart into specific position
		XPropertySet xPropSet = null;

		XChartDocument xchartdocument = (XChartDocument) UnoRuntime
				.queryInterface(XChartDocument.class, xinterface);
		XDiagram xdiagram = (XDiagram) xchartdocument.getDiagram();
		XMultiServiceFactory xmultiservicefactory = (XMultiServiceFactory) UnoRuntime
				.queryInterface(XMultiServiceFactory.class, xchartdocument);
		Object object = xmultiservicefactory
				.createInstance("com.sun.star.chart.BarDiagram");
		xdiagram = (XDiagram) UnoRuntime.queryInterface(XDiagram.class, object);
		xchartdocument.setDiagram(xdiagram);

		// set main title
		XShape xShape = (XShape) UnoRuntime.queryInterface(XShape.class,
				xtablechart);
		xShape = xchartdocument.getTitle();
		xPropSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
				xShape);
		xPropSet.setPropertyValue("String", new String(title));
		xPropSet.setPropertyValue("CharHeight", new Float(fontSize));
		xPropSet.setPropertyValue("CharWeight", new Float(
				com.sun.star.awt.FontWeight.BOLD));

		xShape = (XShape) UnoRuntime.queryInterface(XShape.class, xtablechart);
		xShape = xchartdocument.getLegend();
		xPropSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
				xShape);
		xPropSet.setPropertyValue("CharHeight", new Float(fontSize));

		return xtablechart;
	}

	/**
	 * Set the position of the Legend box of a chart.
	 * 
	 * @param xtablechart
	 * @param iPosition
	 *            1=L, 2=R, 3=TOP, 4=BOTTOM
	 * @throws Exception
	 */
	public void setLegendPosition(XTableChart xtablechart, int iPosition)
			throws Exception {
		XEmbeddedObjectSupplier xembeddedobjectsupplier = (XEmbeddedObjectSupplier) UnoRuntime
				.queryInterface(XEmbeddedObjectSupplier.class, xtablechart);
		XInterface xinterface = xembeddedobjectsupplier.getEmbeddedObject();

		// to set the chart into specific position
		XPropertySet xPropSet = null;

		XChartDocument xchartdocument = (XChartDocument) UnoRuntime
				.queryInterface(XChartDocument.class, xinterface);

		XShape xShape = (XShape) UnoRuntime.queryInterface(XShape.class,
				xtablechart);
		xShape = (XShape) UnoRuntime.queryInterface(XShape.class, xtablechart);
		xShape = xchartdocument.getLegend();
		xPropSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
				xShape);

		switch (iPosition) {
		case 1:
			xPropSet.setPropertyValue("Alignment", ChartLegendPosition.LEFT);
			break;
		case 2:
			xPropSet.setPropertyValue("Alignment", ChartLegendPosition.RIGHT);
			break;
		case 3:
			xPropSet.setPropertyValue("Alignment", ChartLegendPosition.TOP);
			break;
		case 4:
			xPropSet.setPropertyValue("Alignment", ChartLegendPosition.BOTTOM);
			break;
		}
	}

	/**
	 * Show/Hide the legend box
	 * 
	 * @param xTableChart
	 * @param bHasLegend
	 * @throws Exception
	 */
	public void showLegend(XTableChart xTableChart, boolean bHasLegend)
			throws Exception {
		XEmbeddedObjectSupplier xEmbeddedObjectSupplier = (XEmbeddedObjectSupplier) UnoRuntime
				.queryInterface(XEmbeddedObjectSupplier.class, xTableChart);
		XInterface xInterface = xEmbeddedObjectSupplier.getEmbeddedObject();
		XChartDocument xChartDoc = (XChartDocument) UnoRuntime.queryInterface(
				XChartDocument.class, xInterface);
		XPropertySet xPropSet = (XPropertySet) UnoRuntime.queryInterface(
				XPropertySet.class, xChartDoc);
		xPropSet.setPropertyValue("HasLegend", new Boolean(bHasLegend));
	}

	/**
	 * Set the Chart Axes (XY) Properties. This function also set all the font
	 * properties to be the default size. It also set the background color of
	 * the chart as GREY. By Default it is WHITE.
	 * 
	 * @param xtablechart
	 *            the xtablechart returned when you create a new chart
	 * @param String
	 *            sXAxis the title for the X Axis
	 * @param String
	 *            sYAxis the title for the Y Axis
	 * @param double max the maximum scale for the Y Axis (Ex. Mofit rating max
	 *        = 4)
	 * @param double step the gap scale for the Y Axis (Ex. Mofit rating gap =
	 *        1, because 1-2-3-4)
	 * @param int iXDegree X-Axis values orientation (set to 0 for default
	 *        value)
	 * @param int iYDegree Y-Axis values orientation (set to 0 for default
	 *        value)
	 * 
	 * @return xtablechart tablechart modified
	 */
	public XTableChart setAxes(XTableChart xtablechart, String sXAxis,
			String sYAxis, double max, double step, int iXDegree, int iYDegree)
			throws Exception {

		long now = System.currentTimeMillis();

		XEmbeddedObjectSupplier xembeddedobjectsupplier = (XEmbeddedObjectSupplier) UnoRuntime
				.queryInterface(XEmbeddedObjectSupplier.class, xtablechart);
		XInterface xinterface = xembeddedobjectsupplier.getEmbeddedObject();

		long now1 = System.currentTimeMillis();
		// System.out.println("1set chart title: " + (now1 - now) / 1000);

		// to set the chart into specific position
		XPropertySet xPropSet = null;

		XChartDocument xchartdocument = (XChartDocument) UnoRuntime
				.queryInterface(XChartDocument.class, xinterface);

		now = System.currentTimeMillis();
		// System.out.println("2set chart title: " + (now - now1) / 1000);

		XDiagram xdiagram = (XDiagram) xchartdocument.getDiagram();
		XMultiServiceFactory xmultiservicefactory = (XMultiServiceFactory) UnoRuntime
				.queryInterface(XMultiServiceFactory.class, xchartdocument);
		Object object = xmultiservicefactory
				.createInstance("com.sun.star.chart.BarDiagram");
		xdiagram = (XDiagram) UnoRuntime.queryInterface(XDiagram.class, object);
		xchartdocument.setDiagram(xdiagram);

		now1 = System.currentTimeMillis();
		// System.out.println("3set chart title: " + (now1 - now) / 1000);

		XShape xShape = (XShape) UnoRuntime.queryInterface(XShape.class,
				xtablechart);
		now = System.currentTimeMillis();
		// System.out.println("4set chart title: " + (now - now1) / 1000);
		// X Axis
		XAxisXSupplier xAxis = (XAxisXSupplier) UnoRuntime.queryInterface(
				XAxisXSupplier.class, xdiagram);
		now1 = System.currentTimeMillis();
		// System.out.println("5set chart title: " + (now1 - now) / 1000);

		if (xAxis != null) {
			xShape = xAxis.getXAxisTitle();
			xPropSet = (XPropertySet) UnoRuntime.queryInterface(
					XPropertySet.class, xShape);
			xPropSet.setPropertyValue("String", new String(sXAxis));
			xPropSet.setPropertyValue("CharHeight", new Float(fontSize));
			xPropSet.setPropertyValue("CharWeight", new Float(
					com.sun.star.awt.FontWeight.BOLD));

			xPropSet = xAxis.getXAxis();
			xPropSet.setPropertyValue("Min", new Double(0));
			xPropSet.setPropertyValue("Max", new Double(max));
			xPropSet.setPropertyValue("StepMain", new Double(1));
			xPropSet.setPropertyValue("CharHeight", new Float(fontSize));
			/**
			 * @see com :: sun :: star :: chart :: ChartAxis (Service) 45 degree
			 *      = 4500
			 */
			xPropSet.setPropertyValue("TextRotation", new Integer(iXDegree));
			xPropSet.setPropertyValue("TextBreak", new Boolean(true));
			// xPropSet.setPropertyValue("Overlap", new Long(0L));
			// xPropSet.setPropertyValue("TextCanOverlap", new Boolean(true));

			// set the xAxis label
			// xPropSet.setPropertyValue("DisplayLabels", new Boolean (false));
		}
		now = System.currentTimeMillis();
		// System.out.println("6set chart title: " + (now - now1) / 1000);
		// Y Axis
		XAxisYSupplier yAxis = (XAxisYSupplier) UnoRuntime.queryInterface(
				XAxisYSupplier.class, xdiagram);
		if (yAxis != null) {

			xShape = yAxis.getYAxisTitle();
			now1 = System.currentTimeMillis();
			// System.out.println("7.1set chart title: " + (now1 - now) / 1000);

			xPropSet = (XPropertySet) UnoRuntime.queryInterface(
					XPropertySet.class, xShape);

			now = System.currentTimeMillis();
			// System.out.println("7.2set chart title: " + (now - now1) / 1000);

			xPropSet.setPropertyValue("String", new String(sYAxis));

			now1 = System.currentTimeMillis();
			// System.out.println("7.3set chart title: " + (now1 - now) / 1000);

			xPropSet.setPropertyValue("CharHeight", new Float(fontSize));

			now = System.currentTimeMillis();
			// System.out.println("7.4set chart title: " + (now - now1) / 1000);

			xPropSet.setPropertyValue("CharWeight", new Float(
					com.sun.star.awt.FontWeight.BOLD));

			now1 = System.currentTimeMillis();
			// System.out.println("7.5set chart title: " + (now1 - now) / 1000);

			xPropSet = yAxis.getYAxis();
			now = System.currentTimeMillis();
			// System.out.println("7.6set chart title: " + (now - now1) / 1000);

			xPropSet.setPropertyValue("Min", new Double(0));
			now1 = System.currentTimeMillis();
			// System.out.println("7.7set chart title: " + (now1 - now) / 1000);

			xPropSet.setPropertyValue("Max", new Double(max));
			now = System.currentTimeMillis();
			// System.out.println("7.8set chart title: " + (now - now1) / 1000);

			xPropSet.setPropertyValue("StepMain", new Double(step));
			now1 = System.currentTimeMillis();
			// System.out.println("7.9set chart title: " + (now1 - now) / 1000);

			/*
			 * xPropSet.setPropertyValue("CharHeight", new Double(charHeight));
			 * now = System.currentTimeMillis();
			 * System.out.println("7.10set chart title: " + (now - now1) /
			 * 1000);
			 * 
			 * xPropSet.setPropertyValue("TextRotation", new Integer(iYDegree));
			 * now1 = System.currentTimeMillis();
			 * System.out.println("7.11set chart title: " + (now1 - now) /
			 * 1000);
			 * 
			 * 
			 * xPropSet.setPropertyValue("TextBreak", new Boolean(true)); now =
			 * System.currentTimeMillis();
			 * System.out.println("7.12set chart title: " + (now - now1) /
			 * 1000);
			 * 
			 * 
			 * xPropSet.setPropertyValue("TextCanOverlap", new Boolean(true));
			 * now1 = System.currentTimeMillis();
			 * System.out.println("7.13set chart title: " + (now1 - now) /
			 * 1000); now = System.currentTimeMillis();
			 * System.out.println("7.14set chart title: " + (now - now1) /
			 * 1000);
			 */
		}

		now1 = System.currentTimeMillis();
		// System.out.println("7set chart title: " + (now1 - now) / 1000);

		// this is to set the BGColor of the Chart Area, comment off if it is
		// unnecessary
		xPropSet = xchartdocument.getArea();
		xPropSet.setPropertyValue("FillStyle",
				com.sun.star.drawing.FillStyle.SOLID);
		xPropSet.setPropertyValue("FillBackground", new Boolean(true));
		xPropSet.setPropertyValue("FillColor", new Integer(16777215)); // orginal
																		// colour
																		// 12632256));

		now = System.currentTimeMillis();
		// System.out.println("8set chart title: " + (now - now1) / 1000);
		return xtablechart;
	}

	/**
	 * Set the Chart Axes (XY) Properties. This function also set all the font
	 * properties to be the default size. It also set the background color of
	 * the chart as GREY. By Default it is WHITE.
	 * 
	 * @param xtablechart
	 *            the xtablechart returned when you create a new chart
	 * @param String
	 *            sXAxis the title for the X Axis
	 * @param String
	 *            sYAxis the title for the Y Axis
	 * @param double max the maximum scale for the Y Axis (Ex. Mofit rating max
	 *        = 4)
	 * @param double step the gap scale for the Y Axis (Ex. Mofit rating gap =
	 *        1, because 1-2-3-4)
	 * @param int iXDegree X-Axis values orientation (set to 0 for default
	 *        value)
	 * @param int iYDegree Y-Axis values orientation (set to 0 for default
	 *        value)
	 * 
	 * @return xtablechart tablechart modified
	 */
	public XTableChart setAxes(XTableChart xtablechart, String sXAxis,
			String sYAxis, double max, double step, int iXDegree, int iYDegree,
			int off) throws Exception {

		long now = System.currentTimeMillis();

		XEmbeddedObjectSupplier xembeddedobjectsupplier = (XEmbeddedObjectSupplier) UnoRuntime
				.queryInterface(XEmbeddedObjectSupplier.class, xtablechart);
		XInterface xinterface = xembeddedobjectsupplier.getEmbeddedObject();

		long now1 = System.currentTimeMillis();
		// System.out.println("1set chart title: " + (now1 - now) / 1000);

		// to set the chart into specific position
		XPropertySet xPropSet = null;

		XChartDocument xchartdocument = (XChartDocument) UnoRuntime
				.queryInterface(XChartDocument.class, xinterface);

		now = System.currentTimeMillis();
		// System.out.println("2set chart title: " + (now - now1) / 1000);

		XDiagram xdiagram = (XDiagram) xchartdocument.getDiagram();
		XMultiServiceFactory xmultiservicefactory = (XMultiServiceFactory) UnoRuntime
				.queryInterface(XMultiServiceFactory.class, xchartdocument);
		Object object = xmultiservicefactory
				.createInstance("com.sun.star.chart.BarDiagram");
		xdiagram = (XDiagram) UnoRuntime.queryInterface(XDiagram.class, object);
		xchartdocument.setDiagram(xdiagram);

		now1 = System.currentTimeMillis();
		// System.out.println("3set chart title: " + (now1 - now) / 1000);

		XShape xShape = (XShape) UnoRuntime.queryInterface(XShape.class,
				xtablechart);
		now = System.currentTimeMillis();
		// System.out.println("4set chart title: " + (now - now1) / 1000);
		// X Axis
		XAxisXSupplier xAxis = (XAxisXSupplier) UnoRuntime.queryInterface(
				XAxisXSupplier.class, xdiagram);
		now1 = System.currentTimeMillis();
		// System.out.println("5set chart title: " + (now1 - now) / 1000);
		// by Hemilda 24/11/2008 to add label for x-axis & y-axis of
		// distribution report
		XPropertySet xAxisProp = (XPropertySet) UnoRuntime.queryInterface(
				XPropertySet.class, xAxis);
		xAxisProp.setPropertyValue("HasXAxisTitle", new Boolean(true));

		if (xAxis != null) {
			xShape = xAxis.getXAxisTitle();

			xPropSet = (XPropertySet) UnoRuntime.queryInterface(
					XPropertySet.class, xShape);
			xPropSet.setPropertyValue("String", new String(sXAxis));
			Integer value = new Integer(90);
			xPropSet.setPropertyValue("TextRotation", value);
			xPropSet.setPropertyValue("CharHeight", new Float(fontSize));
			xPropSet.setPropertyValue("CharWeight", new Float(
					com.sun.star.awt.FontWeight.BOLD));

			xPropSet = xAxis.getXAxis();
			xPropSet.setPropertyValue("Min", new Double(0));
			xPropSet.setPropertyValue("Max", new Double(max));
			xPropSet.setPropertyValue("StepMain", new Double(1));
			xPropSet.setPropertyValue("CharHeight", new Float(fontSize));

			/**
			 * @see com :: sun :: star :: chart :: ChartAxis (Service) 45 degree
			 *      = 4500
			 */
			// xPropSet.setPropertyValue("TextRotation", new Integer(iXDegree));
			// xPropSet.setPropertyValue("TextBreak", new Boolean(true));
			// xPropSet.setPropertyValue("Overlap", new Long(0L));
			// xPropSet.setPropertyValue("TextCanOverlap", new Boolean(true));

			// set the xAxis label
			// xPropSet.setPropertyValue("DisplayLabels", new Boolean (true));
		}
		now = System.currentTimeMillis();
		// System.out.println("6set chart title: " + (now - now1) / 1000);
		// Y Axis
		XAxisYSupplier yAxis = (XAxisYSupplier) UnoRuntime.queryInterface(
				XAxisYSupplier.class, xdiagram);

		XPropertySet yAxisProp = (XPropertySet) UnoRuntime.queryInterface(
				XPropertySet.class, yAxis);
		yAxisProp.setPropertyValue("HasYAxisTitle", new Boolean(true));

		if (yAxis != null) {

			xShape = yAxis.getYAxisTitle();
			// now1 = System.currentTimeMillis();
			// System.out.println("7.1set chart title: " + (now1 - now) / 1000);

			xPropSet = (XPropertySet) UnoRuntime.queryInterface(
					XPropertySet.class, xShape);

			// now = System.currentTimeMillis();
			// System.out.println("7.2set chart title: " + (now - now1) / 1000);

			xPropSet.setPropertyValue("String", new String(sYAxis));
			Integer value = new Integer(-7700);
			xPropSet.setPropertyValue("TextRotation", value);
			// now1 = System.currentTimeMillis();
			// System.out.println("7.3set chart title: " + (now1 - now) / 1000);

			xPropSet.setPropertyValue("CharHeight", new Float(fontSize));

			// now = System.currentTimeMillis();
			// System.out.println("7.4set chart title: " + (now - now1) / 1000);

			xPropSet.setPropertyValue("CharWeight", new Float(
					com.sun.star.awt.FontWeight.BOLD));

			// now1 = System.currentTimeMillis();
			// System.out.println("7.5set chart title: " + (now1 - now) / 1000);

			xPropSet = yAxis.getYAxis();
			// now = System.currentTimeMillis();
			// System.out.println("7.6set chart title: " + (now - now1) / 1000);

			xPropSet.setPropertyValue("Min", new Double(0));
			// now1 = System.currentTimeMillis();
			// System.out.println("7.7set chart title: " + (now1 - now) / 1000);

			xPropSet.setPropertyValue("Max", new Double(max));
			// now = System.currentTimeMillis();
			// System.out.println("7.8set chart title: " + (now - now1) / 1000);

			xPropSet.setPropertyValue("StepMain", new Double(step));
			// now1 = System.currentTimeMillis();
			// System.out.println("7.9set chart title: " + (now1 - now) / 1000);

		}

		now1 = System.currentTimeMillis();
		// System.out.println("7set chart title: " + (now1 - now) / 1000);

		// this is to set the BGColor of the Chart Area, comment off if it is
		// unnecessary
		xPropSet = xchartdocument.getArea();
		xPropSet.setPropertyValue("FillStyle",
				com.sun.star.drawing.FillStyle.SOLID);
		xPropSet.setPropertyValue("FillBackground", new Boolean(true));
		xPropSet.setPropertyValue("FillColor", new Integer(16777215)); // original
																		// colour
																		// (12632256));

		now = System.currentTimeMillis();
		// System.out.println("8set chart title: " + (now - now1) / 1000);
		return xtablechart;
	}

	/*
	 * Set the colour of the bar in the Bar graph
	 * 
	 * @param xtablechart the xtablechart returned when you create a new chart
	 * 
	 * @param colColor colour of the bar graph
	 * 
	 * @return boolean isSuccessful
	 * 
	 * @author: Mark Oei
	 * 
	 * @since v.1.3.12.68 (25 Mar 2010)
	 */
	public boolean setBarColor(XTableChart xtablechart, int colColor)
			throws Exception {
		// to set the chart into specific position
		XPropertySet xPropSet = null;
		boolean isSuccessful = true;
		try {
			XEmbeddedObjectSupplier xembeddedobjectsupplier = (XEmbeddedObjectSupplier) UnoRuntime
					.queryInterface(XEmbeddedObjectSupplier.class, xtablechart);
			XInterface xinterface = xembeddedobjectsupplier.getEmbeddedObject();

			XChartDocument xchartdocument = (XChartDocument) UnoRuntime
					.queryInterface(XChartDocument.class, xinterface);

			XMultiServiceFactory xmultiservicefactory = (XMultiServiceFactory) UnoRuntime
					.queryInterface(XMultiServiceFactory.class, xchartdocument);

			XDiagram xdiagram = (XDiagram) xchartdocument.getDiagram();

			Object object = xmultiservicefactory
					.createInstance("com.sun.star.chart.BarDiagram");

			xdiagram = (XDiagram) UnoRuntime.queryInterface(XDiagram.class,
					object);
			xchartdocument.setDiagram(xdiagram);

			// this is to set the BGColor of the Chart Area, comment off if it
			// is unnecessary
			xPropSet = xchartdocument.getArea();
			xPropSet.setPropertyValue("FillStyle",
					com.sun.star.drawing.FillStyle.SOLID);
			xPropSet.setPropertyValue("FillBackground", new Boolean(true));
			xPropSet.setPropertyValue("FillColor", new Integer(16777215)); // white
																			// background

			// Set the color of the bar graph
			xPropSet = xdiagram.getDataRowProperties(0);
			xPropSet.setPropertyValue("FillColor", new Integer(colColor)); // 0x9999FF));
			xPropSet.setPropertyValue("LineColor", new Integer(0));

		} catch (Exception ex) {
			System.out.println("OpenOffice.java - setBarColor - "
					+ ex.getMessage());
			isSuccessful = false;
		}
		return isSuccessful;
	} // End of setBarColor method

	/*
	 * Set the colour of the bar in the Bar graph
	 * 
	 * @param xtablechart the xtablechart returned when you create a new chart
	 * 
	 * @param colColor1 first colour of the bar graph
	 * 
	 * @param colColor2 second colour of the bar graph
	 * 
	 * @param totalBar total no of bars in the graph
	 * 
	 * @return boolean isSuccessful
	 * 
	 * @author: Albert
	 * 
	 * @since v.1.3.15.33 (11 July 2012)
	 */
	public boolean setBarTwoColor(XTableChart xtablechart, int colColor,
			int colColor2, int totalBar) throws Exception {
		// to set the chart into specific position
		XPropertySet xPropSet = null;
		boolean isSuccessful = true;
		try {
			XEmbeddedObjectSupplier xembeddedobjectsupplier = (XEmbeddedObjectSupplier) UnoRuntime
					.queryInterface(XEmbeddedObjectSupplier.class, xtablechart);
			XInterface xinterface = xembeddedobjectsupplier.getEmbeddedObject();

			XChartDocument xchartdocument = (XChartDocument) UnoRuntime
					.queryInterface(XChartDocument.class, xinterface);

			XMultiServiceFactory xmultiservicefactory = (XMultiServiceFactory) UnoRuntime
					.queryInterface(XMultiServiceFactory.class, xchartdocument);

			XDiagram xdiagram = (XDiagram) xchartdocument.getDiagram();

			Object object = xmultiservicefactory
					.createInstance("com.sun.star.chart.BarDiagram");

			xdiagram = (XDiagram) UnoRuntime.queryInterface(XDiagram.class,
					object);
			xchartdocument.setDiagram(xdiagram);

			// this is to set the BGColor of the Chart Area, comment off if it
			// is unnecessary
			xPropSet = xchartdocument.getArea();
			xPropSet.setPropertyValue("FillStyle",
					com.sun.star.drawing.FillStyle.SOLID);
			xPropSet.setPropertyValue("FillBackground", new Boolean(true));
			xPropSet.setPropertyValue("FillColor", new Integer(16777215)); // white
																			// background

			// Set the color of the bar graph
			for (int i = 0; i < totalBar; i++) {
				xPropSet = xdiagram.getDataPointProperties(i, 0);
				if (i % 2 == 0)
					xPropSet.setPropertyValue("FillColor",
							new Integer(colColor));
				else
					xPropSet.setPropertyValue("FillColor", new Integer(
							colColor2));
				xPropSet.setPropertyValue("LineColor", new Integer(0));
			}

		} catch (Exception ex) {
			System.out.println("OpenOffice.java - setBarColor - "
					+ ex.getMessage());
			isSuccessful = false;
		}
		return isSuccessful;
	} // End of setBarColor method

	
	
	/*
	 * Method to draw the gridlines (vertical and horizontal) for bar chart only
	 * Use setTableProperties to control which lines will be displayed.
	 * 
	 * @param xtablechart the xtablechart returned when you create a new chart
	 * 
	 * @param gridLineColor colour of the main grid lines
	 * 
	 * @return boolean isSuccessful
	 * 
	 * @author: Mark Oei
	 * 
	 * @since v.1.3.12.68 (25 Mar 2010)
	 */
	public boolean drawGridLines(XTableChart xtablechart, int gridLineColor)
			throws Exception {
		// to set the chart into specific position
		XPropertySet xPropSet = null;
		boolean isSuccessful = true;
		try {
			XEmbeddedObjectSupplier xembeddedobjectsupplier = (XEmbeddedObjectSupplier) UnoRuntime
					.queryInterface(XEmbeddedObjectSupplier.class, xtablechart);
			XInterface xinterface = xembeddedobjectsupplier.getEmbeddedObject();

			XChartDocument xchartdocument = (XChartDocument) UnoRuntime
					.queryInterface(XChartDocument.class, xinterface);

			XMultiServiceFactory xmultiservicefactory = (XMultiServiceFactory) UnoRuntime
					.queryInterface(XMultiServiceFactory.class, xchartdocument);

			XDiagram xdiagram = (XDiagram) xchartdocument.getDiagram();

			// change com.sun.star.chart.BarDiagram to
			// com.sun.star.chart.LineDiagram for line chart
			Object object = xmultiservicefactory
					.createInstance("com.sun.star.chart.BarDiagram");

			xdiagram = (XDiagram) UnoRuntime.queryInterface(XDiagram.class,
					object);
			xchartdocument.setDiagram(xdiagram);
			// this is to set the outer lines of the plot area
			X3DDisplay x3DDisplay = (X3DDisplay) UnoRuntime.queryInterface(
					X3DDisplay.class, xdiagram);
			if (x3DDisplay != null) {
				xPropSet = x3DDisplay.getFloor();
				xPropSet.setPropertyValue("LineStyle",
						com.sun.star.drawing.LineStyle.SOLID);
				xPropSet.setPropertyValue("LineColor", new Integer(0));
				xPropSet = x3DDisplay.getWall();
				xPropSet.setPropertyValue("LineStyle",
						com.sun.star.drawing.LineStyle.SOLID);
				xPropSet.setPropertyValue("LineColor", new Integer(0));
			}
			XAxisXSupplier xAxis = (XAxisXSupplier) UnoRuntime.queryInterface(
					XAxisXSupplier.class, xdiagram);
			if (xAxis != null) {
				// Draw main gridlines
				xPropSet = xAxis.getXMainGrid();
				xPropSet.setPropertyValue("LineColor", new Integer(
						gridLineColor));
				// Draw x-axis line
				xPropSet = xAxis.getXAxis();
				xPropSet.setPropertyValue("LineColor", new Integer(0));

			}
			XAxisYSupplier yAxis = (XAxisYSupplier) UnoRuntime.queryInterface(
					XAxisYSupplier.class, xdiagram);
			if (yAxis != null) {
				// Draw main gridlines
				xPropSet = yAxis.getYMainGrid();
				xPropSet.setPropertyValue("LineColor", new Integer(
						gridLineColor));
				// Draw y-axis line
				xPropSet = yAxis.getYAxis();
				xPropSet.setPropertyValue("LineColor", new Integer(0));
			}
		} catch (Exception ex) {
			System.out.println("OpenOffice.java - setGridLines - "
					+ ex.getMessage());
			isSuccessful = false;
		}
		return isSuccessful;
	} // End of drawGridLines method

	/**
	 * Set the Chart Axes (XY) Properties. This function also set all the font
	 * properties to be the default size. It also set the background color of
	 * the chart as GREY. By Default it is WHITE. Min and Max value for the axis
	 * are determined by Excel automatically
	 * 
	 * @param xtablechart
	 *            the xtablechart returned when you create a new chart
	 * @param sXAxis
	 *            the title for the X Axis
	 * @param sYAxis
	 *            the title for the Y Axis
	 * @param int iXDegree X-Axis values orientation (set to 0 for default
	 *        value)
	 * @param int iYDegree Y-Axis values orientation (set to 0 for default
	 *        value)
	 * 
	 * @return xtablechart tablechart modified
	 */
	public XTableChart setAxes(XTableChart xtablechart, String sXAxis,
			String sYAxis, boolean bMin, boolean bMax, boolean bStep,
			int iXDegree, int iYDegree) throws Exception {

		XEmbeddedObjectSupplier xembeddedobjectsupplier = (XEmbeddedObjectSupplier) UnoRuntime
				.queryInterface(XEmbeddedObjectSupplier.class, xtablechart);
		XInterface xinterface = xembeddedobjectsupplier.getEmbeddedObject();

		// to set the chart into specific position
		XPropertySet xPropSet = null;

		XChartDocument xchartdocument = (XChartDocument) UnoRuntime
				.queryInterface(XChartDocument.class, xinterface);

		XDiagram xdiagram = (XDiagram) xchartdocument.getDiagram();
		XMultiServiceFactory xmultiservicefactory = (XMultiServiceFactory) UnoRuntime
				.queryInterface(XMultiServiceFactory.class, xchartdocument);
		Object object = xmultiservicefactory
				.createInstance("com.sun.star.chart.BarDiagram");
		xdiagram = (XDiagram) UnoRuntime.queryInterface(XDiagram.class, object);
		xchartdocument.setDiagram(xdiagram);

		XShape xShape = (XShape) UnoRuntime.queryInterface(XShape.class,
				xtablechart);

		XAxisXSupplier xAxis = (XAxisXSupplier) UnoRuntime.queryInterface(
				XAxisXSupplier.class, xdiagram);
		if (xAxis != null) {
			xShape = xAxis.getXAxisTitle();
			xPropSet = (XPropertySet) UnoRuntime.queryInterface(
					XPropertySet.class, xShape);
			xPropSet.setPropertyValue("String", new String(sXAxis));
			xPropSet.setPropertyValue("CharHeight", new Float(fontSize));
			xPropSet.setPropertyValue("CharWeight", new Float(
					com.sun.star.awt.FontWeight.BOLD));

			xPropSet = xAxis.getXAxis();
			xPropSet.setPropertyValue("AutoMin", new Boolean(bMin));
			xPropSet.setPropertyValue("AutoMax", new Boolean(bMax));
			xPropSet.setPropertyValue("AutoStepMain", new Boolean(bStep));
			xPropSet.setPropertyValue("CharHeight", new Float(fontSize));
			xPropSet.setPropertyValue("TextRotation", new Integer(iXDegree));
		}

		// set title for X Axis
		XAxisYSupplier yAxis = (XAxisYSupplier) UnoRuntime.queryInterface(
				XAxisYSupplier.class, xdiagram);
		if (yAxis != null) {

			xShape = yAxis.getYAxisTitle();
			xPropSet = (XPropertySet) UnoRuntime.queryInterface(
					XPropertySet.class, xShape);
			xPropSet.setPropertyValue("String", new String(sYAxis));
			xPropSet.setPropertyValue("CharHeight", new Float(fontSize));
			xPropSet.setPropertyValue("CharWeight", new Float(
					com.sun.star.awt.FontWeight.BOLD));

			xPropSet = yAxis.getYAxis();
			xPropSet.setPropertyValue("AutoMin", new Boolean(bMin));
			xPropSet.setPropertyValue("AutoMax", new Boolean(bMax));
			xPropSet.setPropertyValue("AutoStepMain", new Boolean(bStep));
			xPropSet.setPropertyValue("CharHeight", new Double(charHeight));
			xPropSet.setPropertyValue("TextRotation", new Integer(iYDegree));
		}

		// this is to set the BGColor of the Chart Area, comment off if it is
		// unnecessary
		xPropSet = xchartdocument.getArea();
		xPropSet.setPropertyValue("FillStyle",
				com.sun.star.drawing.FillStyle.SOLID);
		xPropSet.setPropertyValue("FillBackground", new Boolean(true));
		xPropSet.setPropertyValue("FillColor", new Integer(16777215)); // original
																		// colour
																		// 12632256));

		return xtablechart;
	}

	/**
	 * Set the other Chart Properties. This function is to set whether the chart
	 * needs to display grids for the XY Axes.
	 * 
	 * @param xtablechart
	 *            the xtablechart returned when you create a new chart
	 * @param xGrid
	 *            specify display/not display the X Axis Grid
	 * @param yGrid
	 *            specify display/not display the Y Axis Grid
	 * @param xTitle
	 *            specify display/not display the X Axis Title
	 * @param yTitle
	 *            specify display/not display the Y Axis Title
	 * @param vertical
	 *            true = vertical, false = horizontal
	 * 
	 * @return xtablechart tablechart modified
	 */
	public void setChartProperties(XTableChart xtablechart, boolean xGrid,
			boolean yGrid, boolean xTitle, boolean yTitle, boolean vertical)
			throws Exception {
		XEmbeddedObjectSupplier xembeddedobjectsupplier = (XEmbeddedObjectSupplier) UnoRuntime
				.queryInterface(XEmbeddedObjectSupplier.class, xtablechart);
		XInterface xinterface = xembeddedobjectsupplier.getEmbeddedObject();

		// to set the chart into specific position
		XPropertySet xPropSet = null;

		XChartDocument xchartdocument = (XChartDocument) UnoRuntime
				.queryInterface(XChartDocument.class, xinterface);

		XDiagram xdiagram = (XDiagram) xchartdocument.getDiagram();
		XMultiServiceFactory xmultiservicefactory = (XMultiServiceFactory) UnoRuntime
				.queryInterface(XMultiServiceFactory.class, xchartdocument);

		Object object = xmultiservicefactory
				.createInstance("com.sun.star.chart.BarDiagram");
		xdiagram = (XDiagram) UnoRuntime.queryInterface(XDiagram.class, object);
		xchartdocument.setDiagram(xdiagram);
		xPropSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
				object);
		xPropSet.setPropertyValue("HasXAxisTitle", new Boolean(xTitle));
		xPropSet.setPropertyValue("HasYAxisTitle", new Boolean(yTitle));
		xPropSet.setPropertyValue("HasXAxisGrid", new Boolean(xGrid));
		xPropSet.setPropertyValue("HasYAxisGrid", new Boolean(yGrid));
		xPropSet.setPropertyValue("Vertical", new Boolean(vertical));

		/**
		 * @see com :: sun :: star :: chart :: Diagram must put in 0 as the
		 *      parameter, couldn't figure out why.
		 */
		xPropSet = xdiagram.getDataRowProperties(0);
		xPropSet.setPropertyValue("CharHeight", new Float(fontSize));
		xPropSet.setPropertyValue("DataCaption", new Integer(
				com.sun.star.chart.ChartDataCaption.VALUE));
	}

	/**
	 * This function is to set the new source data for the chart in the
	 * template.
	 * 
	 * @param xSpreadsheet
	 *            the xtablechart returned when you create a new chart
	 * @param XSpreadsheet
	 *            specify display/not display the X Axis Grid
	 * @param chartIndex
	 *            specify display/not display the Y Axis Grid
	 * @param startCol
	 *            specify display/not display the X Axis Title
	 * @param endCol
	 *            specify display/not display the Y Axis Title
	 * @param startRow
	 *            true = vertical, false = horizontal
	 * @param endRow
	 *            true = vertical, false = horizontal
	 * 
	 * @return void
	 */
	public void setSourceData(XSpreadsheet xSpreadsheet,
			XSpreadsheet xSpreadsheet2, int chartIndex, int startCol,
			int endCol, int startRow, int endRow) throws Exception {
		/**
		 * Get the table charts supplier of the spreadsheet. This is basically
		 * get all the charts in the document. You can refer to each of the
		 * charts by using Index, start from 0.
		 * 
		 */
		XTableChartsSupplier xtablechartssupplier = (XTableChartsSupplier) UnoRuntime
				.queryInterface(XTableChartsSupplier.class, xSpreadsheet);

		// Get all table charts of the spreadsheet.
		XTableCharts xtablecharts = xtablechartssupplier.getCharts();

		/**
		 * Get the created table chart by Index.
		 */
		this.xtablechart = (XTableChart) UnoRuntime.queryInterface(
				XTableChart.class, ((XIndexAccess) UnoRuntime.queryInterface(
						XIndexAccess.class, xtablecharts))
						.getByIndex(chartIndex));

		// Get the cell range of the spreadsheet.
		XCellRange xcellrange = (XCellRange) UnoRuntime.queryInterface(
				XCellRange.class, xSpreadsheet);

		/**
		 * Get the cell range of the written values (source data). Set the new
		 * range of source data here
		 */
		XCellRange xcellrangeChart = xSpreadsheet2.getCellRangeByPosition(
				startCol, startRow, endCol, endRow); // xcellrange.getCellRangeByName(
														// "A2:B3");

		// Get the addressable cell range.
		XCellRangeAddressable xcellrangeaddressable = (XCellRangeAddressable) UnoRuntime
				.queryInterface(XCellRangeAddressable.class, xcellrangeChart);

		// Get the cell range address.
		CellRangeAddress cellrangeaddress = xcellrangeaddressable
				.getRangeAddress();

		// Create the cell range address for the chart.
		CellRangeAddress[] cellrangeaddressChart = new CellRangeAddress[1];
		cellrangeaddressChart[0] = cellrangeaddress;

		xtablechart.setRanges(cellrangeaddressChart);

	}

	public XTableChart getChartByIndex(XSpreadsheet xSpreadsheet, int chartIndex)
			throws Exception {
		/**
		 * Get the table charts supplier of the spreadsheet. This is basically
		 * get all the charts in the document. You can refer to each of the
		 * charts by using Index, start from 0.
		 * 
		 */
		XTableChartsSupplier xtablechartssupplier = (XTableChartsSupplier) UnoRuntime
				.queryInterface(XTableChartsSupplier.class, xSpreadsheet);

		// Get all table charts of the spreadsheet.
		XTableCharts xtablecharts = xtablechartssupplier.getCharts();

		/**
		 * Get the created table chart by Index.
		 */
		this.xtablechart = (XTableChart) UnoRuntime.queryInterface(
				XTableChart.class, ((XIndexAccess) UnoRuntime.queryInterface(
						XIndexAccess.class, xtablecharts))
						.getByIndex(chartIndex));

		return this.xtablechart;

	}

	/************************************ CHART *********************************************************/

	/************************************ INSERT IMAGE *********************************************************/

	/**
	 * This function is insert image into the spreadsheet, to a specific cell
	 * location.
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet returned when you called getSheet
	 * @param xDoc
	 *            component returned when you called the openDoc
	 * @param logoName
	 *            the filename of the logo file, must include the extension
	 * @param width
	 *            the width of the image
	 * @param height
	 *            the height of the image
	 * @param row
	 *            the row position where the image will be inserted
	 * @param column
	 *            the column position where the image will be inserted
	 */
	public void insertImage(XSpreadsheet xSpreadsheet, XComponent xDoc,
			String logoName, int width, int height, int row, int column)
			throws java.lang.Exception {

		// Create a drawing page for the image
		XDrawPagesSupplier oDPS = (XDrawPagesSupplier) UnoRuntime
				.queryInterface(XDrawPagesSupplier.class, xDoc);

		XDrawPages oDPn = oDPS.getDrawPages();
		XIndexAccess oDPi = (XIndexAccess) UnoRuntime.queryInterface(
				XIndexAccess.class, oDPn);
		XDrawPage oDrawPage = (XDrawPage) UnoRuntime.queryInterface(
				XDrawPage.class, oDPi.getByIndex(0));

		// get MSF
		XMultiServiceFactory oDocMSF = (XMultiServiceFactory) UnoRuntime
				.queryInterface(XMultiServiceFactory.class, xDoc);

		Object oInt = oDocMSF
				.createInstance("com.sun.star.drawing.GraphicObjectShape");
		XShape oShape = (XShape) UnoRuntime.queryInterface(XShape.class, oInt);

		// Add it to the drawing page.
		oDrawPage.add(oShape);

		XPropertySet xPropSet = null;

		// Get the cell by defining the row and column
		XCell xCell = xSpreadsheet.getCellByPosition(column, row);// column, row

		// get the properties of the cell
		xPropSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
				xCell);

		// Get the XY Coordinates based on row and column, so that we can
		// integrate into the Graphics position
		Point position = (com.sun.star.awt.Point) xPropSet
				.getPropertyValue("Position");

		// Set the image size (width and height)
		Size size = new Size();
		size.Height = height;
		size.Width = width;
		oShape.setSize(size);

		// Set the image position (XY Coordinates);
		oShape.setPosition(position);

		xPropSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
				oInt);

		String cUrl = "file:///" + logoName;
		xPropSet.setPropertyValue("GraphicURL", cUrl);

	}

	/************************************ INSERT IMAGE ENDS *******************************************************/

	/**
	 * Insert the header and footer for the document. We don't need to set Right
	 * Footer here, because the right footer is for page numbers. The page
	 * number is best set on the template since it is static.
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet returned when you called getSheet
	 * @param leftHeader
	 *            the string for left header
	 * @param rightHeader
	 *            the string for right header
	 * @param leftFooter
	 *            the string for left footer
	 * 
	 */
	public void insertHeaderFooter(XComponent xDoc, String leftHeader,
			String rightHeader, String leftFooter) throws java.lang.Exception {

		XModel xModel = (XModel) UnoRuntime.queryInterface(XModel.class, xDoc);
		com.sun.star.style.XStyleFamiliesSupplier xSFS = (com.sun.star.style.XStyleFamiliesSupplier) UnoRuntime
				.queryInterface(
						com.sun.star.style.XStyleFamiliesSupplier.class, xModel);

		com.sun.star.container.XNameAccess xFamilies = xSFS.getStyleFamilies();

		// the element should now contain at least two Styles. The first is
		// "graphics" and the other one is the name of the Master page

		String[] Families = xFamilies.getElementNames();
		Object aFamilyObj = xFamilies.getByName(Families[1]); // page style

		// System.out.println(Families[1]);
		com.sun.star.container.XNameAccess xStyles = (com.sun.star.container.XNameAccess) UnoRuntime
				.queryInterface(com.sun.star.container.XNameAccess.class,
						aFamilyObj);

		String[] Styles = xStyles.getElementNames();
		Object aStyleObj = xStyles.getByName(Styles[2]); // sheet1

		com.sun.star.style.XStyle xStyle = (com.sun.star.style.XStyle) UnoRuntime
				.queryInterface(com.sun.star.style.XStyle.class, aStyleObj);

		XPropertySet xPropSet = (XPropertySet) UnoRuntime.queryInterface(
				XPropertySet.class, xStyle);
		// xPropSet.setPropertyValue("CharFontName", "Times");
		// xPropSet.setPropertyValue("CharHeight", new Float(11));

		// refer to the service com :: sun :: star :: style :: PageStyle
		// must make sure that the header is on, otherwise no header will be
		// displayed
		xPropSet.setPropertyValue("HeaderIsOn", new Boolean(true));

		// HEADER
		Object oHeader = xPropSet.getPropertyValue("LeftPageHeaderContent");
		if (oHeader != null) {
			XHeaderFooterContent xHeader = (XHeaderFooterContent) UnoRuntime
					.queryInterface(XHeaderFooterContent.class, oHeader);

			// center header
			XText xText = (XText) UnoRuntime.queryInterface(XText.class,
					xHeader.getCenterText());
			xText.setString("");

			// left header
			xText = (XText) UnoRuntime.queryInterface(XText.class,
					xHeader.getLeftText());
			xText.setString(leftHeader);

			XTextCursor xTextCursor = xText.createTextCursor();
			XPropertySet xCursorProps = (XPropertySet) UnoRuntime
					.queryInterface(XPropertySet.class, xTextCursor);
			xCursorProps.setPropertyValue("CharHeight", new Float(11));
			xCursorProps.setPropertyValue("CharFontName", "Times");

			// right header
			xText = (XText) UnoRuntime.queryInterface(XText.class,
					xHeader.getRightText());
			xText.setString(rightHeader);
			xTextCursor = xText.createTextCursor();
			xCursorProps = (XPropertySet) UnoRuntime.queryInterface(
					XPropertySet.class, xTextCursor);
			xCursorProps.setPropertyValue("CharHeight", new Float(11));
			xCursorProps.setPropertyValue("CharFontName", "Times");

		}

		// have to reset the properties of the header after you made changes.
		xPropSet.setPropertyValue("RightPageHeaderContent", oHeader);

		// FOOTER
		Object oFooter = xPropSet.getPropertyValue("LeftPageFooterContent");
		if (oFooter != null) {
			XHeaderFooterContent xFooter = (XHeaderFooterContent) UnoRuntime
					.queryInterface(XHeaderFooterContent.class, oFooter);

			// center footer
			XText xText = (XText) UnoRuntime.queryInterface(XText.class,
					xFooter.getCenterText());
			xText.setString("");

			// left footer
			xText = (XText) UnoRuntime.queryInterface(XText.class,
					xFooter.getLeftText());

			xText.setString(leftFooter);

			// create text cursor for selecting and formatting
			XTextCursor xTextCursor = xText.createTextCursor();
			XPropertySet xCursorProps = (XPropertySet) UnoRuntime
					.queryInterface(XPropertySet.class, xTextCursor);

			xCursorProps.setPropertyValue("CharHeight", new Float(11));
			xCursorProps.setPropertyValue("CharFontName", "Times");
			// use cursor to select "iAssess" and apply bold italic
			/*
			 * xTextCursor.gotoStart(false); xTextCursor.goRight((short)1,
			 * true); // from CharacterProperties
			 * xCursorProps.setPropertyValue("CharPosture",
			 * com.sun.star.awt.FontSlant.ITALIC);
			 * xCursorProps.setPropertyValue("CharPosture",
			 * com.sun.star.awt.FontSlant.ITALIC); //
			 * xCursorProps.setPropertyValue("CharWeight", // new
			 * Float(com.sun.star.awt.FontWeight.BOLD));
			 */
			// right footer
			// xText = (XText)
			// UnoRuntime.queryInterface(XText.class,xFooter.getRightText());
			// xText.setString(rightFooter);
		}

		// System.out.println(xPropSet.getPropertyValue("FirstPageNumber"));
		// have to reset the properties of the footer after you made changes.
		xPropSet.setPropertyValue("RightPageFooterContent", oFooter);
	}

	/**
	 * Insert the header and footer for multiple sheets
	 * 
	 * @param xDoc
	 * @param leftHeader
	 *            - the string for left header
	 * @param rightHeader
	 *            - the string for right header
	 * @param leftFooter
	 *            - the string for left footer
	 * @param startSheet
	 *            - the sheet that you want to start inserting header/footer
	 * @param noOfSheet
	 *            - No of total sheets to insert header/footer
	 * @throws java.lang.Exception
	 * @author Maruli
	 */
	public void insertHeaderFooter(XComponent xDoc, String leftHeader,
			String rightHeader, String leftFooter, int startSheet, int noOfSheet)
			throws java.lang.Exception {
		startSheet = startSheet + 1; // First sheet starts from 2

		XModel xModel = (XModel) UnoRuntime.queryInterface(XModel.class, xDoc);
		com.sun.star.style.XStyleFamiliesSupplier xSFS = (com.sun.star.style.XStyleFamiliesSupplier) UnoRuntime
				.queryInterface(
						com.sun.star.style.XStyleFamiliesSupplier.class, xModel);

		com.sun.star.container.XNameAccess xFamilies = xSFS.getStyleFamilies();

		// the element should now contain at least two Styles. The first is
		// "graphics" and the other one is the name of the Master page

		String[] Families = xFamilies.getElementNames();
		Object aFamilyObj = xFamilies.getByName(Families[1]); // page style

		com.sun.star.container.XNameAccess xStyles = (com.sun.star.container.XNameAccess) UnoRuntime
				.queryInterface(com.sun.star.container.XNameAccess.class,
						aFamilyObj);

		String[] Styles = xStyles.getElementNames();
		for (int i = 0; i < noOfSheet; i++) {
			Object aStyleObj = xStyles.getByName(Styles[i + startSheet]);
			
			com.sun.star.style.XStyle xStyle = (com.sun.star.style.XStyle) UnoRuntime
					.queryInterface(com.sun.star.style.XStyle.class, aStyleObj);

			XPropertySet xPropSet = (XPropertySet) UnoRuntime.queryInterface(
					XPropertySet.class, xStyle);

			// refer to the service com :: sun :: star :: style :: PageStyle
			// must make sure that the header is on, otherwise no header will be
			// displayed
			xPropSet.setPropertyValue("HeaderIsOn", new Boolean(true));
			xPropSet.setPropertyValue("FooterIsOn", new Boolean(true));
			xPropSet.setPropertyValue("NumberingType", new Short(
					com.sun.star.style.NumberingType.ARABIC));
			// HEADER
			Object oHeader = xPropSet.getPropertyValue("LeftPageHeaderContent");
			if (oHeader != null) {
				XHeaderFooterContent xHeader = (XHeaderFooterContent) UnoRuntime
						.queryInterface(XHeaderFooterContent.class, oHeader);

				// center header
				XText xText = (XText) UnoRuntime.queryInterface(XText.class,
						xHeader.getCenterText());
				xText.setString("");

				// left header
				xText = (XText) UnoRuntime.queryInterface(XText.class,
						xHeader.getLeftText());
				xText.setString(leftHeader);

				// right header
				xText = (XText) UnoRuntime.queryInterface(XText.class,
						xHeader.getRightText());
				xText.setString(rightHeader);
			}

			// have to reset the properties of the header after you made
			// changes.
			xPropSet.setPropertyValue("RightPageHeaderContent", oHeader);

			// FOOTER
			Object oFooter = xPropSet.getPropertyValue("LeftPageFooterContent");

			if (oHeader != null) {
				XHeaderFooterContent xFooter = (XHeaderFooterContent) UnoRuntime
						.queryInterface(XHeaderFooterContent.class, oFooter);

				// center footer
				XText xText = (XText) UnoRuntime.queryInterface(XText.class,
						xFooter.getCenterText());
				xText.setString("");

				// left footer
				xText = (XText) UnoRuntime.queryInterface(XText.class,
						xFooter.getLeftText());
				xText.setString(leftFooter);
				
				// right footer

				xText = (XText) UnoRuntime.queryInterface(XText.class,
						xFooter.getRightText());

				// xText.setString("");
			}

			// System.out.println(xPropSet.getPropertyValue("FirstPageNumber"));
			// have to reset the properties of the footer after you made
			// changes.
			xPropSet.setPropertyValue("RightPageFooterContent", oFooter);
		}
	}

	/**
	 * Set the row height, this is use for merge cells which contains long
	 * string that requires more than 1 row. OpenOffice can auto set the height,
	 * but excel cannot. So we need to manually set the height.
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet returned when you called getSheet
	 * @param row
	 *            the row that its height is going to be adjusted
	 * @param column
	 *            the starting column to adjust
	 * @param height
	 *            the height of adjusted row, you can use 560 for a height of
	 *            one row.
	 * 
	 */
	public void setRowHeight(XSpreadsheet xSpreadsheet, int row, int column,
			int height) throws java.lang.Exception {

		XCellRange xCellRange = xSpreadsheet.getCellRangeByPosition(column,
				row, column, row);

		com.sun.star.table.XColumnRowRange xColRowRange = (com.sun.star.table.XColumnRowRange) UnoRuntime
				.queryInterface(com.sun.star.table.XColumnRowRange.class,
						xCellRange);

		com.sun.star.table.XTableRows xRows = xColRowRange.getRows();
		Object aRowObj = xRows.getByIndex(0);

		com.sun.star.beans.XPropertySet xPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime
				.queryInterface(com.sun.star.beans.XPropertySet.class, aRowObj);

		xPropSet.setPropertyValue("Height", new Integer(height));
	}
	
	public void setColumnWidth(XSpreadsheet xSpreadsheet, int row, int column,
			int width) throws java.lang.Exception {

		XCellRange xCellRange = xSpreadsheet.getCellRangeByPosition(column,
				row, column, row);

		com.sun.star.table.XColumnRowRange xColRowRange = (com.sun.star.table.XColumnRowRange) UnoRuntime
				.queryInterface(com.sun.star.table.XColumnRowRange.class,
						xCellRange);

		com.sun.star.table.XTableColumns xColumns = xColRowRange.getColumns();
		Object aColObj = xColumns.getByIndex(0);

		com.sun.star.beans.XPropertySet xPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime
				.queryInterface(com.sun.star.beans.XPropertySet.class, aColObj);

		xPropSet.setPropertyValue("Width", new Integer(width));
	}

	/*
	 * Change(s) : New method added to return height of a single row. Reason(s)
	 * : Method used as part of the fix to the problem of a competency table
	 * spilt across two pages. Updated By: Alvis Updated On: 06 Aug 2009
	 */
	/**
	 * Return the row height of a single row
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet returned when you called getSheet
	 * @param row
	 *            the row that its height is going to be returned
	 * @param column
	 *            the column of the row
	 * 
	 */
	public int getRowHeight(XSpreadsheet xSpreadsheet, int row, int column)
			throws java.lang.Exception {

		XCellRange xCellRange = xSpreadsheet.getCellRangeByPosition(column,
				row, column, row);

		com.sun.star.table.XColumnRowRange xColRowRange = (com.sun.star.table.XColumnRowRange) UnoRuntime
				.queryInterface(com.sun.star.table.XColumnRowRange.class,
						xCellRange);

		com.sun.star.table.XTableRows xRows = xColRowRange.getRows();
		Object aRowObj = xRows.getByIndex(0);

		com.sun.star.beans.XPropertySet xPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime
				.queryInterface(com.sun.star.beans.XPropertySet.class, aRowObj);

		Integer x = (Integer) xPropSet.getPropertyValue("Height");
		return x.intValue();

	}// end of getRowHeight()

	/**
	 * This function is to count how many rows required for a long string that
	 * required more than 1 row. It is required because when OO saved into
	 * Excel, Excel didn't have the function to autowrap the merged cells.
	 * 
	 * @param input
	 *            the string to be counted
	 * @param totalChar
	 *            total characters that can be accomodated in a row.
	 * 
	 * @return total number of rows required.
	 */
	public int countTotalRow(String input, double totalChar) {

		String split[] = input.split("\n");
		double merge = 0;
		int totalRow = 0;

		int isThai = input.indexOf("&#");

		// This function was used in order to save into excel, it didn't auto
		// wrap \n when saving into Excel.
		// it uses split, because it needs to count linefeed as 1 row.
		for (int i = 0; i < split.length; i++) {

			merge = (double) split[i].length() / (double) totalChar;

			if (isThai != -1)
				merge = merge / 4;

			int rounded = (int) Math.round(merge);

			totalRow += rounded;

			if (rounded < merge) // decimal point counted as 1 row
				totalRow++;
		}
	
		return totalRow;
	}

	/**
	 * Delete rows. OO allows deletion of multiple rows at the same time,
	 * therefore you need to provied startCol, endCol, startRow, endRow to
	 * specify the CellRange where the insertion is done.
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet returned when you called getSheet function
	 * @param startCol
	 *            the column where the deletion starts
	 * @param endCol
	 *            the column where the deletion ends
	 * @param startRow
	 *            the row where the deletion starts
	 * @param endRow
	 *            the row where the deletion ends
	 * @totalRows total rows to be deleted
	 * @param row
	 *            the row where the deletion is done (normally is first row from
	 *            the range) Ex: If you specify the range of A6 to A10, this
	 *            means that the deletion can only be done within this range.
	 *            From the range A6-A10, there are 5 rows. So, you can only
	 *            insert maximum 5 rows, totalRows <= 5. If row = 1, this means
	 *            that the deletion starts at A6, row=2, starts at A7.
	 */
	public void deleteRows(XSpreadsheet xSpreadsheet, int startCol, int endCol,
			int startRow, int endRow, int totalRows, int row)
			throws java.lang.Exception {

		XCellRange xCellRange = xSpreadsheet.getCellRangeByPosition(startCol,
				startRow, endCol, endRow);

		com.sun.star.table.XColumnRowRange xColRowRange = (com.sun.star.table.XColumnRowRange) UnoRuntime
				.queryInterface(com.sun.star.table.XColumnRowRange.class,
						xCellRange);

		// insert row
		com.sun.star.table.XTableRows xRows = xColRowRange.getRows();

		xRows.removeByIndex(row, totalRows); // row, count
	}

	/**
	 * Close a document after it has been saved. Remember to close the document,
	 * otherwise the OpenOffice application will still running in the
	 * background. Passing in the opened document.
	 * 
	 * @param xRemoteServiceManager
	 *            the service manager returned when you get the service manager
	 */
	public void closeDoc(XComponent xDoc) throws java.lang.Exception {
		// Conditions: xDocument = m_xLoadedDocument
		// Check supported functionality of the document (model or controller).
		com.sun.star.frame.XModel xModel = (com.sun.star.frame.XModel) UnoRuntime
				.queryInterface(com.sun.star.frame.XModel.class, xDoc);

		if (xModel != null) {
			// It is a full featured office document.
			// Try to use close mechanism instead of a hard dispose().
			// But maybe such service is not available on this model.
			com.sun.star.util.XCloseable xCloseable = (com.sun.star.util.XCloseable) UnoRuntime
					.queryInterface(com.sun.star.util.XCloseable.class, xModel);

			if (xCloseable != null) {
				// use close(boolean DeliverOwnership)
				// The boolean parameter DeliverOwnership tells objects vetoing
				// the close process that they may
				// assume ownership if they object the closure by throwing a
				// CloseVetoException
				// Here we give up ownership. To be on the safe side, catch
				// possible veto exception anyway.
				xCloseable.close(true);
			}
		}
		// If close is not supported by this model - try to dispose it.
		// But if the model disagree with a reset request for the modify state
		// we shouldn't do so. Otherwise some strange things can happen.
		else {
			com.sun.star.lang.XComponent xDisposeable = (com.sun.star.lang.XComponent) UnoRuntime
					.queryInterface(com.sun.star.lang.XComponent.class, xModel);
			xDisposeable.dispose();
		}
	}

	public void setFontSize(float size) {
		this.fontSize = size;
	}

	/**
	 * Font Settings, set the font name/type.
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet returned when you called getSheet function
	 * @param startCol
	 *            the column where the range starts
	 * @param endCol
	 *            the column where the range ends
	 * @param startRow
	 *            the row where the range starts
	 * @param endRow
	 *            the row where the range ends Added By: Chun Pong Date: 18 Jun
	 *            2008 Remarks: Copied From iSelect
	 */
	public void setFontType(XSpreadsheet xSpreadsheet, int startCol,
			int endCol, int startRow, int endRow, String type)
			throws java.lang.Exception {

		XCellRange xCellRange = xSpreadsheet.getCellRangeByPosition(startCol,
				startRow, endCol, endRow);

		// look at Service CharacterProperties
		com.sun.star.beans.XPropertySet xPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime
				.queryInterface(com.sun.star.beans.XPropertySet.class,
						xCellRange);

		// set the font color of the cell range
		xPropSet.setPropertyValue("CharFontName", type);
	}

	/*
	 * Change(s) : New method added for auto text wrapping for a cell. Reason(s)
	 * : Used primarily for fixing row height problems with candidates' remarks
	 * portion of the individual report Updated By: Alvis Updated On: 28 Aug
	 * 2009
	 */

	/**
	 * Auto Wrap Cell Text
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet returned when you called getSheet function
	 * @param startCol
	 *            the column where the merging starts
	 * @param endCol
	 *            the column where the merging ends
	 * @param startRow
	 *            the row where the merging starts
	 * @param endRow
	 *            the row where the merging ends
	 * @author Desmond
	 */
	public void wrapText(XSpreadsheet xSpreadsheet, int startCol, int endCol,
			int startRow, int endRow) throws java.lang.Exception {

		// Wrap text
		XCellRange xCellRange = xSpreadsheet.getCellRangeByPosition(startCol,
				startRow, endCol, endRow);
		// look at Service CellProperties
		com.sun.star.beans.XPropertySet xPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime
				.queryInterface(com.sun.star.beans.XPropertySet.class,
						xCellRange);
		xPropSet.setPropertyValue("IsTextWrapped", new Boolean(true));

		// Set row range
		com.sun.star.table.XColumnRowRange xColRowRange = (com.sun.star.table.XColumnRowRange) UnoRuntime
				.queryInterface(com.sun.star.table.XColumnRowRange.class,
						xCellRange);

		// Retrieve row
		com.sun.star.table.XTableRows xRows = xColRowRange.getRows();
		Object aRowObj = xRows.getByIndex(0);

		// Retrieve properties set for the row
		xPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime.queryInterface(
				com.sun.star.beans.XPropertySet.class, aRowObj);

		// Set row to use OptimalHeight, i.e. Autofit row height
		xPropSet.setPropertyValue("OptimalHeight", new Boolean(true));

		// Set alignment
		setCellAllignment(xSpreadsheet, startCol, endCol, startRow, endRow, 1,
				1); // Horizontal, Left
		setCellAllignment(xSpreadsheet, startCol, endCol, startRow, endRow, 2,
				1); // top

	} // end wrapText()

	/**
	 * Auto unWrap Cell Text
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet returned when you called getSheet function
	 * @param startCol
	 *            the column where the merging starts
	 * @param endCol
	 *            the column where the merging ends
	 * @param startRow
	 *            the row where the merging starts
	 * @param endRow
	 *            the row where the merging ends
	 * @author Desmond
	 */
	public void unWrapText(XSpreadsheet xSpreadsheet, int startCol, int endCol,
			int startRow, int endRow) throws java.lang.Exception {

		// Wrap text
		XCellRange xCellRange = xSpreadsheet.getCellRangeByPosition(startCol,
				startRow, endCol, endRow);
		// look at Service CellProperties
		com.sun.star.beans.XPropertySet xPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime
				.queryInterface(com.sun.star.beans.XPropertySet.class,
						xCellRange);
		xPropSet.setPropertyValue("IsTextWrapped", new Boolean(false));

		// Set row range
		com.sun.star.table.XColumnRowRange xColRowRange = (com.sun.star.table.XColumnRowRange) UnoRuntime
				.queryInterface(com.sun.star.table.XColumnRowRange.class,
						xCellRange);

		// Retrieve row
		com.sun.star.table.XTableRows xRows = xColRowRange.getRows();
		Object aRowObj = xRows.getByIndex(0);

		// Retrieve properties set for the row
		xPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime.queryInterface(
				com.sun.star.beans.XPropertySet.class, aRowObj);

		// Set alignment
		setCellAllignment(xSpreadsheet, startCol, endCol, startRow, endRow, 1,
				1); // Horizontal, Left
		setCellAllignment(xSpreadsheet, startCol, endCol, startRow, endRow, 2,
				1); // top

	} // end unwrapText()

	/**
	 * This function is to change the title of the chart in the template given
	 * the chartIndex
	 * 
	 * @param xSpreadsheet
	 *            the xtablechart returned when you create a new chart
	 * @param chartIndex
	 *            specify display/not display the Y Axis Grid
	 * @param title
	 *            the title you want to change to
	 * 
	 * @return void
	 * @author Qiao Li 22 Dec 2009
	 */
	public void changeChartTitle(XSpreadsheet xSpreadsheet, int chartIndex,
			String title) throws UnknownPropertyException,
			PropertyVetoException, IllegalArgumentException,
			WrappedTargetException, IndexOutOfBoundsException {
		XTableChartsSupplier xtablechartssupplier = (XTableChartsSupplier) UnoRuntime
				.queryInterface(XTableChartsSupplier.class, xSpreadsheet);

		XTableCharts xtablecharts = xtablechartssupplier.getCharts();

		XTableChart xtablechart = (XTableChart) UnoRuntime.queryInterface(
				XTableChart.class, ((XIndexAccess) UnoRuntime.queryInterface(
						XIndexAccess.class, xtablecharts))
						.getByIndex(chartIndex));

		changeChartTitle(xtablechart, title);
	}

	/**
	 * This function is to change the title of the chart in the template given
	 * the chartIndex
	 * 
	 * @param xtablechart
	 *            the chart that you want to change
	 * @param title
	 *            the title you want to change to
	 * 
	 * @return xtablechart that has been modified
	 * @author Qiao Li 24 Dec 2009
	 */
	public XTableChart changeChartTitle(XTableChart xtablechart, String title)
			throws UnknownPropertyException, PropertyVetoException,
			IllegalArgumentException, WrappedTargetException,
			IndexOutOfBoundsException {
		XEmbeddedObjectSupplier xembeddedobjectsupplier = (XEmbeddedObjectSupplier) UnoRuntime
				.queryInterface(XEmbeddedObjectSupplier.class, xtablechart);
		XInterface xinterface = xembeddedobjectsupplier.getEmbeddedObject();

		XChartDocument xchartdocument = (XChartDocument) UnoRuntime
				.queryInterface(XChartDocument.class, xinterface);
		XShape xShape = (XShape) UnoRuntime.queryInterface(XShape.class,
				xtablechart);
		xShape = xchartdocument.getTitle();
		XPropertySet xPropSet = (XPropertySet) UnoRuntime.queryInterface(
				XPropertySet.class, xShape);
		xPropSet.setPropertyValue("String", title);
		return xtablechart;
	}

	/**
	 * change the Chart Axes (XY)'s maximum scales the maximum scales will
	 * remain unchanged if scales are set to less than 0 (e.g. -1)
	 * 
	 * @param xSpreadsheet
	 *            the xtablechart returned when you create a new chart
	 * @param chartIndex
	 *            specify display/not display the Y Axis Grid
	 * @param double XMax the maximum scale for the X Axis
	 * @param double YMax the maximum scale for the Y Axis
	 * 
	 * @return xtablechart tablechart modified
	 * @author Qiao Li 23 Dec 2009
	 */
	public XTableChart changeAxesMax(XSpreadsheet xSpreadsheet, int chartIndex,
			double XMax, double YMax) throws Exception {

		XTableChart xtablechart = getChartWithIdx(xSpreadsheet, chartIndex);
		xtablechart = changeAxesMax(xtablechart, XMax, YMax);
		return xtablechart;
	}

	/**
	 * change the Chart Axes (XY)'s maximum scales the maximum scales will
	 * remain unchanged if scales are set to less than 0 (e.g. -1)
	 * 
	 * @param xtablechart
	 *            the tablechart you want to change
	 * @param double XMax the maximum scale for the X Axis
	 * @param double YMax the maximum scale for the Y Axis
	 * 
	 * @return xtablechart tablechart modified
	 * @author Qiao Li 24 Dec 2009
	 */
	public XTableChart changeAxesMax(XTableChart xtablechart, double XMax,
			double YMax) throws UnknownPropertyException,
			PropertyVetoException, IllegalArgumentException,
			WrappedTargetException {
		XEmbeddedObjectSupplier xembeddedobjectsupplier = (XEmbeddedObjectSupplier) UnoRuntime
				.queryInterface(XEmbeddedObjectSupplier.class, xtablechart);
		XInterface xinterface = xembeddedobjectsupplier.getEmbeddedObject();

		XChartDocument xchartdocument = (XChartDocument) UnoRuntime
				.queryInterface(XChartDocument.class, xinterface);
		XShape xShape = (XShape) UnoRuntime.queryInterface(XShape.class,
				xtablechart);

		XPropertySet xPropSet = null;

		XDiagram xdiagram = (XDiagram) xchartdocument.getDiagram();

		// X Axis
		XAxisXSupplier xAxis = (XAxisXSupplier) UnoRuntime.queryInterface(
				XAxisXSupplier.class, xdiagram);

		if (xAxis != null && XMax >= 0) {
			xShape = xAxis.getXAxisTitle();
			xPropSet = (XPropertySet) UnoRuntime.queryInterface(
					XPropertySet.class, xShape);

			xPropSet = xAxis.getXAxis();
			xPropSet.setPropertyValue("Max", new Double(XMax));

		}

		// Y Axis
		XAxisYSupplier yAxis = (XAxisYSupplier) UnoRuntime.queryInterface(
				XAxisYSupplier.class, xdiagram);
		if (yAxis != null && YMax >= 0) {

			xShape = yAxis.getYAxisTitle();

			xPropSet = (XPropertySet) UnoRuntime.queryInterface(
					XPropertySet.class, xShape);

			xPropSet = yAxis.getYAxis();
			xPropSet.setPropertyValue("Max", new Double(YMax));

		}
		return xtablechart;

	}

	/**
	 * change the Chart Axes (XY)'s Min scales the maximum scales will remain
	 * unchanged if scales are set to less than 0 (e.g. -1)
	 * 
	 * @param xtablechart
	 *            the tablechart you want to change
	 * @param double XMax the maximum scale for the X Axis
	 * @param double YMax the maximum scale for the Y Axis
	 * 
	 * @return xtablechart tablechart modified
	 * @author Sherman June 2013
	 */
	public XTableChart changeAxesMin(XTableChart xtablechart, double XMin,
			double YMin) throws UnknownPropertyException,
			PropertyVetoException, IllegalArgumentException,
			WrappedTargetException {
		XEmbeddedObjectSupplier xembeddedobjectsupplier = (XEmbeddedObjectSupplier) UnoRuntime
				.queryInterface(XEmbeddedObjectSupplier.class, xtablechart);
		XInterface xinterface = xembeddedobjectsupplier.getEmbeddedObject();

		XChartDocument xchartdocument = (XChartDocument) UnoRuntime
				.queryInterface(XChartDocument.class, xinterface);
		XShape xShape = (XShape) UnoRuntime.queryInterface(XShape.class,
				xtablechart);

		XPropertySet xPropSet = null;

		XDiagram xdiagram = (XDiagram) xchartdocument.getDiagram();

		// X Axis
		XAxisXSupplier xAxis = (XAxisXSupplier) UnoRuntime.queryInterface(
				XAxisXSupplier.class, xdiagram);

		if (xAxis != null && XMin >= 0) {
			xShape = xAxis.getXAxisTitle();
			xPropSet = (XPropertySet) UnoRuntime.queryInterface(
					XPropertySet.class, xShape);

			xPropSet = xAxis.getXAxis();
			xPropSet.setPropertyValue("Min", new Double(XMin));

		}

		// Y Axis
		XAxisYSupplier yAxis = (XAxisYSupplier) UnoRuntime.queryInterface(
				XAxisYSupplier.class, xdiagram);
		if (yAxis != null && YMin >= 0) {

			xShape = yAxis.getYAxisTitle();

			xPropSet = (XPropertySet) UnoRuntime.queryInterface(
					XPropertySet.class, xShape);

			xPropSet = yAxis.getYAxis();
			xPropSet.setPropertyValue("Min", new Double(YMin));

		}
		return xtablechart;

	}

	/**
	 * This method returns a tablechart that is already in the xSpreadSheet with
	 * chartIndex
	 * 
	 * @param xSpreadsheet
	 * @param chartIndex
	 *            the index of the chart in the xSpreadsheet
	 * @return XTableChart
	 * 
	 * @author Qiao Li 24 Dec 2009
	 */
	public XTableChart getChartWithIdx(XSpreadsheet xSpreadsheet, int chartIndex) {
		XTableChartsSupplier xtablechartssupplier = (XTableChartsSupplier) UnoRuntime
				.queryInterface(XTableChartsSupplier.class, xSpreadsheet);

		XTableCharts xtablecharts = xtablechartssupplier.getCharts();

		XTableChart xtablechart = null;
		try {
			xtablechart = (XTableChart) UnoRuntime.queryInterface(
					XTableChart.class, ((XIndexAccess) UnoRuntime
							.queryInterface(XIndexAccess.class, xtablecharts))
							.getByIndex(chartIndex));

		} catch (IndexOutOfBoundsException e) {
			System.out.println("OpenOffice.java - getChartByIdx");
			e.printStackTrace();
		} catch (WrappedTargetException e) {
			System.out.println("OpenOffice.java - getChartByIdx");
			e.printStackTrace();
		}
		return xtablechart;
	}

	/**
	 * Font Settings, set the font size/height.
	 * 
	 * @param xSpreadsheet
	 *            the specific sheet returned when you called getSheet function
	 * @param startCol
	 *            the column where the range starts
	 * @param endCol
	 *            the column where the range ends
	 * @param startRow
	 *            the row where the range starts
	 * @param endRow
	 *            the row where the range ends
	 */
	public void setFontHeight(XSpreadsheet xSpreadsheet, int startCol,
			int endCol, int startRow, int endRow, float height)
			throws java.lang.Exception {

		XCellRange xCellRange = xSpreadsheet.getCellRangeByPosition(startCol,
				startRow, endCol, endRow);

		// look at Service CharacterProperties
		com.sun.star.beans.XPropertySet xPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime
				.queryInterface(com.sun.star.beans.XPropertySet.class,
						xCellRange);

		// set the font color of the cell range
		xPropSet.setPropertyValue("CharHeight", new Float(height));

		// When setting Font Size/Height, set font size/height for Asian text as
		// well, Desmond 28 June 2010
		xPropSet.setPropertyValue("CharHeightAsian", new Float(height));
	}

	public void justify(XSpreadsheet xSpreadsheet, int startCol, int endCol,
			int startRow, int endRow) throws java.lang.Exception {
		XCellRange xCellRange = xSpreadsheet.getCellRangeByPosition(startCol,
				startRow, endCol, endRow);

		// look at Service CharacterProperties
		com.sun.star.beans.XPropertySet xPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime
				.queryInterface(com.sun.star.beans.XPropertySet.class,
						xCellRange);

		// set the font color of the cell range
		xPropSet.setPropertyValue("HoriJustify", CellHoriJustify.BLOCK);
	}
}