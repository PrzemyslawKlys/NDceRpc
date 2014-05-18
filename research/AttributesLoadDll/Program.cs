
using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.CompilerServices;

namespace AttributesLoadDll
{
	
	public class Program
	{
		public static void Main(){
			Console.WriteLine("Started");
			printWcfLoaded();
			
			var type = System.Reflection.Assembly.GetExecutingAssembly().GetType("AttributesLoadDll.IWithAttribute");
			Console.WriteLine("Loaded {0} marked with attributes",type.FullName);
			printWcfLoaded();
			
		    var attrsData = type.GetCustomAttributesData();
			Console.WriteLine("After GetCustomAttributesData was called on type items");
			printWcfLoaded();
			
			
			var attrs = type.GetCustomAttributes(false);
			Console.WriteLine("After GetCustomAttributes returned {0} items", attrs.Length);
			printWcfLoaded();
			
			wcfType();
			printWcfLoaded();
			
			Console.ReadKey();
		}
		
		[System.Runtime.CompilerServices.MethodImpl(MethodImplOptions.NoInlining)]
		private static void wcfType(){
			Console.WriteLine("Direct touch to {0}",typeof(System.ServiceModel.ServiceContractAttribute));
		}
		
		private static void printWcfLoaded(){
			var loaded = AppDomain.CurrentDomain.GetAssemblies().Where(x=> x.FullName.Contains("System.ServiceModel")).FirstOrDefault();
			Console.WriteLine("Loaded: {0}{1}" ,loaded == null? "null" : loaded.ToString(),Environment.NewLine);
			
		}
	}
}