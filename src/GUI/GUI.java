package GUI;

import util.Arch;
import util.ArchTomasulo;

public class GUI {
	private DynamicTableGUI _estacaoReserva, _registradores, _bufferReord, _memory, _status;
	private ArchTomasulo _arch;
	public GUI(ArchTomasulo arch) {
		_arch = arch;
		String[] estacaoReservaTitle = new String[] {"ID", "Tipo", "Busy", "Instrução", "Vj", "Vk", "Qj", "Qk", "A", "Estado", "Dest."};
		Object[][] estacaoReservaData = populateData("estacao");
		_estacaoReserva = new DynamicTableGUI("Estação Reserva", estacaoReservaTitle, estacaoReservaData);
		
		String[] registradoresTitle = new String[] {"Nome", "Qi", "Vi"};
		Object[][] registradoresData = populateData("registradores"); 
		_registradores = new DynamicTableGUI ("Registradores", registradoresTitle, registradoresData);
		/*
		String[] bufferReordTitle = new String[] {"Entrada", "Ocupado", "Instrução", "Estado", "Destino", "Valor"};
		Object[][] bufferReordData = populateData("rob");
		_bufferReord = new DynamicTableGUI ("Buffer de Reordenação", bufferReordTitle, bufferReordData);
		
		String[] memoryTitle = new String[] {"Endereço", "Valor"};
		Object[][] memoryData =  populateData("memory"); 
		_memory = new DynamicTableGUI ("Memória Recente Usada", memoryTitle, memoryData);*/
		
		String[] statusTitle = new String[] {"Clock corrente", "PC", "Número de Instruções Concluídas", "CPI"};
		Object[][] statusData = populateData("status"); 
		_status = new DynamicTableGUI ("Status", statusTitle, statusData);
	
 	}
	private Object[][] populateData(String string) {
		Object[][] data = null;
		if (string == "estacao") {
			data = new Object [_arch.getNumberOfRS()][];
			for (int i = 0; i < _arch.getNumberOfRS(); i++) {
				data[i] = _arch.getRS()[i].getInfo();
			}
		} else if (string == "registradores") {
			data = new Object[32][];
			for (int i = 0; i < 32; i++) {
				data[i] = Arch.r.getInfo(i);
			}
		} else if (string == "status") {
			data = new Object[1][];
			data[0] = _arch.getProgramInfo();
		}
		return data;
	}
	public void runCycle() {
		Object[][] estacaoData = populateData("estacao");
		_estacaoReserva.updateTable(estacaoData);
		
		Object[][] registradoresData = populateData("registradores"); 
		_registradores.updateTable(registradoresData);
		/*
		Object[][] bufferReordData = populateData("rob");
		_bufferReord.updateTable(bufferReordData);
		
		Object[][] memoryData =  populateData("memory"); 
		_memory.updateTable(memoryData);*/
		
		Object[][] statusData = populateData("status");
		_status.updateTable(statusData);
		
	}
	public void setRunning(boolean b) {
		// TODO Auto-generated method stub
		
	}

}
