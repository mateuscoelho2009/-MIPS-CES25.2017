package GUI;

import util.ArchTomassulo;

public class GUI {
	private DynamicTableGUI _estacaoReserva, _registradores, _bufferReord, _memory, _status;
	private ControlButtonGUI _controlButton;
	private ArchTomassulo _arch;
	public GUI(ArchTomassulo arch) {
		_arch = arch;
		String[] estacaoReservaTitle = new String[] {"ID", "Tipo", "Busy", "Instru��o", "Dest.", "Vj", "Vk", "Qj", "Qk", "A"};
		Object[][] estacaoReservaData = populateData("estacao"); //TODO: PopulateData	
		_estacaoReserva = new DynamicTableGUI("Esta��o Reserva", estacaoReservaTitle, estacaoReservaData);
		
		String[] registradoresTitle = new String[] {"Nome", "Qi", "Vi"};
		Object[][] registradoresData = populateData("registradores"); 
		_registradores = new DynamicTableGUI ("Registradores", registradoresTitle, registradoresData);
		
		String[] bufferReordTitle = new String[] {"Entrada", "Ocupado", "Instru��o", "Estado", "Destino", "Valor"};
		Object[][] bufferReordData = populateData("rob");
		_bufferReord = new DynamicTableGUI ("Buffer de Reordena��o", bufferReordTitle, bufferReordData);
		
		String[] memoryTitle = new String[] {"Endere�o", "Valor"};
		Object[][] memoryData =  populateData("memory"); 
		_memory = new DynamicTableGUI ("Mem�ria Recente Usada", memoryTitle, memoryData);
		
		String[] statusTitle = new String[] {"Clock corrente", "PC", "N�mero de Instru��es Conclu�das", "CPI"};
		Object[][] statusData = populateData("status"); 
		_status = new DynamicTableGUI ("Status", statusTitle, statusData);
		
		_controlButton = new ControlButtonGUI(this);
 	}
	private Object[][] populateData(String string) {
		// TODO Auto-generated method stub
		return null;
	}
	public void runCycle() {
		Object[][] estacaoData = populateData("estacao");
		_estacaoReserva.updateTable(estacaoData);
		
		Object[][] registradoresData = populateData("registradores"); 
		_registradores.updateTable(registradoresData);
		
		Object[][] bufferReordData = populateData("rob");
		_bufferReord.updateTable(bufferReordData);
		
		Object[][] memoryData =  populateData("memory"); 
		_memory.updateTable(memoryData);
		
		Object[][] statusData = populateData("status");
		_status.updateTable(statusData);
		
	}
	public void setRunning(boolean b) {
		// TODO Auto-generated method stub
		
	}

}