package GUI;

import java.awt.Container;

import javax.swing.JFrame;

import util.Arch;
import util.ArchTomasulo;

public class GUI {
	private DynamicTableGUI _estacaoReserva, _registradores, _bufferReord, _memory, _status;
	private ArchTomasulo _arch;
	public GUI(ArchTomasulo arch) {
		int xPos = 0;
		int yPos = 0;
		_arch = arch;
		String[] estacaoReservaTitle = new String[] {"ID", "Tipo", "Busy", "Instrução", "Vj", "Vk", "Qj", "Qk", "A", "Estado", "Dest."};
		Object[][] estacaoReservaData = populateData("estacao");
		_estacaoReserva = new DynamicTableGUI("Estação Reserva", estacaoReservaTitle, estacaoReservaData, xPos, yPos);
		xPos += _estacaoReserva.getWidth();
		String[] registradoresTitle = new String[] {"Nome", "Qi", "Vi"};
		Object[][] registradoresData = populateData("registradores"); 
		_registradores = new DynamicTableGUI ("Registradores", registradoresTitle, registradoresData, xPos, yPos);
		xPos += _registradores.getWidth();
		
		String[] bufferReordTitle = new String[] {"Instrução", "Estado", "Validado", "Resultado"};
		Object[][] bufferReordData = populateData("rob");
		_bufferReord = new DynamicTableGUI ("Buffer de Reordenação", bufferReordTitle, bufferReordData, xPos, yPos);
		
		xPos = 0;
		yPos = _bufferReord.getHeight();
		String[] memoryTitle = new String[] {"Endereço", "Valor Bin", "Valor Int"};
		Object[][] memoryData =  populateData("memory"); 
		_memory = new DynamicTableGUI ("Memória Recente Usada", memoryTitle, memoryData, xPos, yPos);
		xPos += _memory.getWidth();
		String[] statusTitle = new String[] {"Clock corrente", "PC", "Número de Instruções Concluídas", "CPI"};
		Object[][] statusData = populateData("status"); 
		_status = new DynamicTableGUI ("Status", statusTitle, statusData, xPos, yPos);
	
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
		} else if (string == "memory") {
			data = Arch.m.getMemoryInfo();
		} else if (string == "rob") {
			data = _arch.getReorderBuffer().getListInfo();
		}
		return data;
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
		
		
	}
	public void removeFrames() {
		_memory.dispose();
		_status.dispose();
		_estacaoReserva.dispose();
		_status.dispose();
		_registradores.dispose();
		
	}
}
