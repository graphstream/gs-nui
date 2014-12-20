#include <glm/mat4x4.hpp> // glm::mat4
#include <glm/ext.hpp>
#include <GL/gl.h>
#include <iostream>
#include <random>
#include <stdexcept>
#include <string>
#include <chrono>

//
// Compil with :
// > gcc -lm -lstdc++ -o test-glm -std=gnu++11 test-glm.cpp
//

int main(int argc, char** argv) {
	unsigned seed = std::chrono::system_clock::now().time_since_epoch().count();
	std::default_random_engine gen (seed);
	std::uniform_real_distribution<double> d(-64.0,64.0);
	
	int maxIte = 100;
	
	if (argc > 1) {
		try {
    		maxIte = std::stoi(std::string(argv[1]));
		}
		catch (const std::invalid_argument& ia) {
			std::cerr << "Invalid argument: " << ia.what() << '\n';
			return 1;
		}
	}
		
	
	for (int ite = 0; ite < maxIte; ite++) {
		glm::mat4 mat(
			d(gen), d(gen), d(gen), d(gen),
			d(gen), d(gen), d(gen), d(gen),
			d(gen), d(gen), d(gen), d(gen),
			d(gen), d(gen), d(gen), d(gen));
	
		// cout << glm::to_string(mat) << endl;
	
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				std::cout << mat[i][j] << (i == 3 && j == 3 ? "" : " ");
	
		std::cout << std::endl;
	
		glm::mat4 inv = glm::inverse(mat);
	
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				std::cout << inv[i][j] << (i == 3 && j == 3 ? "" : " ");
	
		// cout << glm::to_string(inv) << endl;
		std::cout << std::endl;
		std::cout << std::endl;
	}
	
	return 0;
}

